///*
// * Copyright (c) 2020. Taktik SA, All rights reserved.
// */
//
package org.taktik.icure.security
//
//import kotlinx.coroutines.flow.toList
//import kotlinx.coroutines.reactor.mono
//import org.slf4j.LoggerFactory
//import org.springframework.context.support.MessageSourceAccessor
//import org.springframework.security.authentication.BadCredentialsException
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
//import org.springframework.security.core.Authentication
//import org.springframework.security.core.SpringSecurityMessageSource
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.util.Assert
//import org.taktik.icure.asyncdao.HealthcarePartyDAO
//import org.taktik.icure.asyncdao.UserDAO
//import org.taktik.icure.asynclogic.VersionLogic
//import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
//import org.taktik.icure.constants.Users
//import org.taktik.icure.entities.User
//import org.taktik.icure.entities.getDataOwnerTypeOrNull
//import org.taktik.icure.exceptions.Invalid2FAException
//import org.taktik.icure.exceptions.InvalidJwtException
//import org.taktik.icure.exceptions.Missing2FAException
//import org.taktik.icure.security.jwt.*
//import org.taktik.icure.security.AbstractAuthenticationManager.Companion.UserAccumulator
//import org.taktik.icure.security.AbstractAuthenticationManager.Companion.PasswordValidationStatus
//import org.taktik.icure.utils.debug
//import reactor.core.publisher.Mono
//import java.util.*
//
//// TODO: This is the implementation for OSS only
//class CustomAuthenticationManager(
//	passwordEncoder: PasswordEncoder,
//	jwtUtils: JwtUtils,
//	versionLogic: VersionLogic,
//	healthcarePartyDAO: HealthcarePartyDAO,
//    private val userDAO: UserDAO, //prevent cyclic dependencies,
//) : AbstractAuthenticationManager<BaseJwtDetails, BaseJwtRefreshDetails>(healthcarePartyDAO, passwordEncoder, jwtUtils) {
//	private val messageSourceAccessor: MessageSourceAccessor = SpringSecurityMessageSource.getAccessor()
//	private val log = LoggerFactory.getLogger(javaClass)
//
//	companion object {
//		data class LocalUserAccumulator(
//			val user: User? = null,
//			val groupId: String? = null,
//			val totpFailedUsers: List<User> = listOf(),
//			val totpMissingUsers: List<User> = listOf()
//		) : UserAccumulator
//	}
//
//	private suspend fun buildAuthenticationToken(
//		accumulatedUsers: LocalUserAccumulator,
//		authentication: Authentication?,
//	): UsernamePasswordAuthenticationToken {
//
//		val user = checkNotNull(accumulatedUsers.user) {
//			"No matching user available for login"
//		}
//
//		val hcpHierarchy = accumulatedUsers.user.healthcarePartyId?.let { hcpId ->
//			val datastore: IDatastoreInformation = //TODO()
//			healthcarePartyDAO.get(datastore, hcpId)?.let { baseHcp ->
//				getHcpHierarchy(baseHcp, datastore).map { it.id }
//			} ?: emptyList()
//		} ?: emptyList()
//
//		val userDetails = BaseJwtDetails(
//			userId = user.id,
//			dataOwnerId = user.healthcarePartyId
//				?: user.patientId
//				?: user.deviceId,
//			dataOwnerType = user.getDataOwnerTypeOrNull(),
//			hcpHierarchy = hcpHierarchy,
//			minimumKrakenVersion = versionLogic.checkGroupVersion(versionLogic.getSemanticVersion()),
//		)
//
//		return UsernamePasswordAuthenticationToken(
//			userDetails,
//			authentication,
//			emptySet()
//		)
//	}
//
//	override fun encodedJwtToAuthentication(encodedJwt: String) =
//		JwtAuthenticationToken(
//			claims = jwtUtils.decodeAndGetDetails<BaseJwtDetails>(encodedJwt).also { details ->
//				versionLogic.checkVersion(details.minimumKrakenVersion)
//			},
//			encodedJwt = encodedJwt,
//			authorities = mutableSetOf(),
//			authenticated = true
//		)
//	override fun authenticateWithUsernameAndPassword(
//		authentication: Authentication,
//		groupId: String?
//	): Mono<Authentication> = mono {
//		authentication.principal ?: throw BadCredentialsException("Invalid username or password")
//		Assert.isInstanceOf(
//			UsernamePasswordAuthenticationToken::class.java, authentication,
//			messageSourceAccessor.getMessage(
//				"AbstractUserDetailsAuthenticationProvider.onlySupports",
//				"Only UsernamePasswordAuthenticationToken is supported"
//			)
//		)
//
//		val username = authentication.name
//		val isFullToken = username.matches(Regex("(.+/)([0-9a-zA-Z]{8}-?[0-9a-zA-Z]{4}-?[0-9a-zA-Z]{4}-?[0-9a-zA-Z]{4}-?[0-9a-zA-Z]{12}|idUser_.+)"))
//		val isPartialToken = username.matches(Regex("[0-9a-zA-Z]{8}-?[0-9a-zA-Z]{4}-?[0-9a-zA-Z]{4}-?[0-9a-zA-Z]{4}-?[0-9a-zA-Z]{12}|idUser_.+"))
//
//		val datastoreInformation: IDatastoreInformation = // TODO()
//
//		val users = when {
//			isFullToken -> {
//				listOfNotNull(userDAO.get(datastoreInformation, username.replace(Regex("(.+/)"), "")))
//			}
//			isPartialToken -> {
//				listOfNotNull(userDAO.get(datastoreInformation, username))
//			}
//			else -> {
//				userDAO.listUsersByUsername(datastoreInformation, username).toList() + try {
//					userDAO.listUsersByEmail(datastoreInformation,username).toList()
//				} catch (e: Exception) {
//					log.warn("Error while loading user by email", e)
//					emptyList()
//				} + try {
//					userDAO.listUsersByPhone(datastoreInformation, username).toList()
//				} catch (e: Exception) {
//					log.warn("Error while loading user by phone", e)
//					emptyList()
//				}
//			}
//		}.filter { it.status == Users.Status.ACTIVE && it.deletionDate == null }.sortedBy { it.id }.distinctBy { it.id }
//
//		val password: String = authentication.credentials.toString()
//
//		val accumulatedUsers = users.fold(LocalUserAccumulator()) { acc, candidate ->
//			when (isPasswordValid(candidate, password)) {
//				is PasswordValidationStatus.Success -> acc.copy(user = candidate)
//				PasswordValidationStatus.Failure -> acc.also { log.debug { "No match for $username" }}
//				PasswordValidationStatus.Missing2fa -> acc.copy(totpMissingUsers = acc.totpMissingUsers + candidate)
//				PasswordValidationStatus.Failed2fa -> acc.copy(totpFailedUsers = acc.totpFailedUsers + candidate)
//			}
//		}
//
//		if (accumulatedUsers.totpMissingUsers.isNotEmpty()) {
//			throw Missing2FAException("Missing verification code")
//		}
//
//		if (accumulatedUsers.totpFailedUsers.isNotEmpty()) {
//			throw Invalid2FAException("Invalid verification code")
//		}
//
//		if (accumulatedUsers.user == null) {
//			if (log.isWarnEnabled) {
//				log.warn("Invalid username or password for user $username, no user matched out of ${users.size} candidates")
//			}
//
//			if (accumulatedUsers.totpMissingUsers.isNotEmpty()) {
//				throw Missing2FAException("Missing verification code")
//			}
//
//			if (accumulatedUsers.totpFailedUsers.isNotEmpty()) {
//				throw Invalid2FAException("Invalid verification code")
//			}
//
//			throw BadCredentialsException("Invalid username or password")
//		}
//
//		buildAuthenticationToken(accumulatedUsers, authentication)
//	}
//
//	override suspend fun regenerateJwtDetails(
//		encodedRefreshToken: String,
//		bypassRefreshValidityCheck: Boolean,
//		totpToken: String?
//	): BaseJwtDetails {
//		val jwtRefreshDetails = jwtUtils.decodeRefreshToken(BaseJwtRefreshDetails, encodedRefreshToken)
//
//		val datastore: IDatastoreInformation = // TODO
//		val user = userDAO.findUserOnUserDb(datastore, jwtRefreshDetails.userId, false)
//			?: throw InvalidJwtException("Cannot refresh authentication token for this user")
//		if (user.status != Users.Status.ACTIVE || user.deletionDate != null) throw InvalidJwtException("Cannot create access token for non-active user")
//
//		val hcpHierarchy = user.healthcarePartyId?.let { hcpId ->
//				healthcarePartyDAO.get(datastore, hcpId)?.let { baseHcp ->
//					getHcpHierarchy(baseHcp, datastore).map { it.id }
//				} ?: emptyList()
//		} ?: emptyList()
//
//		return BaseJwtDetails(
//			userId = jwtRefreshDetails.userId,
//			dataOwnerId = user.healthcarePartyId
//				?: user.patientId
//				?: user.deviceId,
//			dataOwnerType = user.getDataOwnerTypeOrNull(),
//			hcpHierarchy = hcpHierarchy,
//			minimumKrakenVersion = versionLogic.checkGroupVersion(versionLogic.getSemanticVersion()),
//		)
//	}
//}
