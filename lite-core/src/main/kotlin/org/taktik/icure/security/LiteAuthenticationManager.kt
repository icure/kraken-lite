package org.taktik.icure.security

import kotlinx.coroutines.flow.toList
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.SpringSecurityMessageSource
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import org.taktik.icure.asyncdao.HealthcarePartyDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.datastore.DatastoreInstanceProvider
import org.taktik.icure.constants.Roles.GrantedAuthority.Companion.ROLE_HCP
import org.taktik.icure.constants.Roles.GrantedAuthority.Companion.ROLE_USER
import org.taktik.icure.constants.Users
import org.taktik.icure.entities.User
import org.taktik.icure.entities.getDataOwnerTypeOrNull
import org.taktik.icure.exceptions.Invalid2FAException
import org.taktik.icure.exceptions.InvalidJwtException
import org.taktik.icure.exceptions.Missing2FAException
import org.taktik.icure.exceptions.PasswordTooShortException
import org.taktik.icure.exceptions.UnauthorizedRequestException
import org.taktik.icure.properties.AuthenticationProperties
import org.taktik.icure.security.AbstractAuthenticationManager.Companion.PasswordValidationStatus.Failed2fa
import org.taktik.icure.security.AbstractAuthenticationManager.Companion.PasswordValidationStatus.Missing2fa
import org.taktik.icure.security.jwt.BaseJwtDetails
import org.taktik.icure.security.jwt.BaseRefreshJwtConverter
import org.taktik.icure.security.jwt.JwtAuthentication
import org.taktik.icure.security.jwt.JwtDetails
import org.taktik.icure.security.jwt.JwtRefreshDetails
import org.taktik.icure.security.jwt.JwtUtils
import org.taktik.icure.utils.error
import java.net.URI
import java.net.URL

interface LiteAuthenticationManager : CustomReactiveAuthenticationManager {
    suspend fun loginWithCloudJwt(issuer: String, cloudToken: String): JwtAuthentication
}

@Service
class LiteAuthenticationManagerImpl(
    private val datastoreInstanceProvider: DatastoreInstanceProvider,
    private val userDAO: UserDAO,
    private val authenticationProperties: AuthenticationProperties,
    private val refreshJwtConverter: BaseRefreshJwtConverter,
    healthcarePartyDAO: HealthcarePartyDAO,
    passwordEncoder: PasswordEncoder,
    jwtUtils: JwtUtils,
    private val cloudJwtValidator: CloudJwtValidator,
    jwtValidator: CloudJwtValidator,
) : AbstractAuthenticationManager<JwtDetails, JwtRefreshDetails>(
    healthcarePartyDAO,
    passwordEncoder,
    jwtUtils
), LiteAuthenticationManager {
    private val messageSourceAccessor = SpringSecurityMessageSource.getAccessor()

    override suspend fun encodedJwtToAuthentication(encodedJwt: String): JwtAuthentication =
        LiteJwtAuthentication(jwtUtils.validateAndDecodeAuthDetails(BaseJwtDetails, encodedJwt))

    override suspend fun regenerateAuthJwt(
        encodedRefreshToken: String,
        bypassRefreshValidityCheck: Boolean,
        totpToken: String?
    ): Pair<JwtDetails, Long?> {
        val jwtRefreshDetails = jwtUtils.validateAndDecodeRefreshToken(refreshJwtConverter, encodedRefreshToken)
        val datastoreInformation = datastoreInstanceProvider.getInstanceAndGroup()
        val user = userDAO.findUserOnUserDb(datastoreInformation, jwtRefreshDetails.userId, false)
            ?: throw InvalidJwtException("Cannot refresh authentication token for this user")
        if (user.status != Users.Status.ACTIVE || user.deletionDate != null) throw InvalidJwtException("Cannot create access token for non-active user")
        val hcpHierarchy = user.healthcarePartyId?.let { hcpId ->
            healthcarePartyDAO.get(datastoreInformation, hcpId)?.let { baseHcp ->
                getHcpHierarchy(baseHcp, datastoreInformation).map { it.id }
            } ?: emptyList()
        } ?: emptyList()

        return BaseJwtDetails(
            userId = jwtRefreshDetails.userId,
            dataOwnerId = user.healthcarePartyId
                ?: user.patientId
                ?: user.deviceId,
            dataOwnerType = user.getDataOwnerTypeOrNull(),
            hcpHierarchy = hcpHierarchy,
            authorities = setOfNotNull(
                SimpleGrantedAuthority(ROLE_USER),
                SimpleGrantedAuthority(ROLE_HCP).takeIf { user.healthcarePartyId != null },
            )
        ) to jwtRefreshDetails.jwtDuration
    }

    override suspend fun checkAuthentication(fullGroupAndId: String, password: String) {
        if (password.length < authenticationProperties.minPasswordLength && !password.matches(TOKEN_REGEX)) {
            throw BadCredentialsException("Password too short")
        }
        val datastoreInformation = datastoreInstanceProvider.getInstanceAndGroup()
        return listOfNotNull(userDAO.findUserOnUserDb(datastoreInformation, fullGroupAndId, false))
            .filter { it.status == Users.Status.ACTIVE && it.deletionDate != null }.toSet()
            .sortedWith(compareBy({ it.groupId }, { it.id }))
            .let { candidates ->
                candidates.fold(false) { result, candidate ->
                    result || isPasswordValid(candidate, password).isSuccess()
                }.let { result ->
                    if (!result) {
                        throw BadCredentialsException("Invalid username or password")
                    }
                }
            }
    }

    override suspend fun authenticateWithUsernameAndPassword(
        authentication: Authentication,
        groupId: String?,
        applicationId: String?
    ): JwtAuthentication {
        val datastoreInformation = datastoreInstanceProvider.getInstanceAndGroup()

        authentication.principal ?: throw BadCredentialsException("Invalid username or password")
        authentication.name?.takeIf { it.isNotBlank() } ?: throw BadCredentialsException("Invalid username or password")
        authentication.credentials ?: throw BadCredentialsException("Invalid username or password")

        val username: String = authentication.name

        val password = authentication.credentials.toString()
        if (password.length < authenticationProperties.minPasswordLength && !password.matches(TOKEN_REGEX)) {
            throw PasswordTooShortException("Password too short")
        }
        if (password.length < authenticationProperties.recommendedPasswordLength && !password.matches(TOKEN_REGEX)) {
            log.error { "$username is attempting to login with a short password" }
        }

        Assert.isInstanceOf(
            UsernamePasswordAuthenticationToken::class.java, authentication,
            messageSourceAccessor.getMessage(
                "AbstractUserDetailsAuthenticationProvider.onlySupports",
                "Only UsernamePasswordAuthenticationToken is supported"
            )
        )
        val users = (
                (userDAO.findUserOnUserDb(datastoreInformation, username, false)?.let { listOf(it) } ?: emptyList())
                        + try {
                    userDAO.listUsersByUsername(datastoreInformation, username).toList()
                } catch (_: Exception) {
                    emptyList()
                }
                        + try {
                    userDAO.listUsersByEmail(datastoreInformation, username).toList()
                } catch (_: Exception) {
                    emptyList()
                }
                        + try {
                    userDAO.listUsersByPhone(datastoreInformation, username).toList()
                } catch (_: Exception) {
                    emptyList()
                }
                )
            .filter { it.status == Users.Status.ACTIVE && it.deletionDate == null }
            .toList()
            .sortedBy { it.id }
            .distinctBy { it.id }

        val accumulatedUsers = users.map { isPasswordValid(it, password) to it }

        if (accumulatedUsers.none { (it, _) -> it.isSuccess() }) {
            throw when {
                accumulatedUsers.any { (it, _) -> it == Missing2fa } -> Missing2FAException("Missing verification code")
                accumulatedUsers.any { (it, _) -> it == Failed2fa } -> Invalid2FAException("Invalid verification code")
                else -> BadCredentialsException("Invalid username or password")
            }
        }

        return buildAuthenticationToken(accumulatedUsers.first { it.first.isSuccess() }.second)
    }

    private suspend fun buildAuthenticationToken(user: User): LiteJwtAuthentication {
        val datastoreInformation = datastoreInstanceProvider.getInstanceAndGroup()

        val hcpHierarchy = user.healthcarePartyId?.let {
            healthcarePartyDAO.get(datastoreInformation, it)?.let { baseHcp ->
                getHcpHierarchy(baseHcp, datastoreInformation).map { hcp ->  hcp.id }
            }
        } ?: emptyList()

        val authorities = setOfNotNull(
            SimpleGrantedAuthority(ROLE_USER),
            SimpleGrantedAuthority(ROLE_HCP).takeIf { user.healthcarePartyId != null },
        )

        val userDetails = BaseJwtDetails(
            userId = user.id,
            dataOwnerId = user.healthcarePartyId
                ?: user.patientId
                ?: user.deviceId,
            dataOwnerType = user.getDataOwnerTypeOrNull(),
            hcpHierarchy = hcpHierarchy,
            authorities = authorities
        )

        return LiteJwtAuthentication(
            userDetails
        )
    }

    override suspend fun loginWithCloudJwt(
        issuer: String,
        cloudToken: String
    ): JwtAuthentication {
        val userId = cloudJwtValidator.validateCloudJwtAndExtractUserId(issuer, cloudToken)
        return buildAuthenticationToken(
            userDAO.get(datastoreInstanceProvider.getInstanceAndGroup(), userId)
                ?: throw UnauthorizedRequestException("User for provided cloud jwt was not found; make sure provided jwt is for the correct group.")
        )
    }
}