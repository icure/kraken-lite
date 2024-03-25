package org.taktik.icure.security.jwt.impl

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.taktik.icure.entities.DataOwnerType
import org.taktik.icure.entities.security.jwt.JwtResponse
import org.taktik.icure.security.jwt.BaseJwtRefreshDetails
import org.taktik.icure.security.jwt.JwtDetails
import org.taktik.icure.security.jwt.JwtRefreshDetails
import org.taktik.icure.security.jwt.JwtToResponseMapper
import org.taktik.icure.security.jwt.JwtUtils
import org.taktik.icure.services.external.rest.v1.dto.AuthenticationResponse

@Component
class LiteJwtToResponseMapperImpl(
    private val jwtUtils: JwtUtils
) : JwtToResponseMapper {
    private fun authenticationToJwtDetails(authentication: Authentication, jwtDuration: Long?): Pair<JwtDetails, JwtRefreshDetails> {
        val details = authentication.principal as JwtDetails
        val refreshDetails = BaseJwtRefreshDetails(
            userId = details.userId,
            jwtDuration = jwtDuration ?: jwtUtils.defaultExpirationTimeMillis,
        )
        return Pair(details, refreshDetails)
    }
    override fun toAuthenticationResponse(authentication: Authentication, username: String, jwtDuration: Long?): AuthenticationResponse {
        val (details, refreshDetails) = authenticationToJwtDetails(authentication, jwtDuration)
        return AuthenticationResponse(
            successful = true,
            token = jwtUtils.createJWT(details, jwtDuration),
            refreshToken = jwtUtils.createRefreshJWT(refreshDetails),
            healthcarePartyId = details.dataOwnerId.takeIf { details.dataOwnerType == DataOwnerType.HCP },
            dataOwnerId = details.dataOwnerId,
            groupId = null,
            userId = details.userId,
            username = username
        )
    }

    override fun toJwtResponse(authentication: Authentication, jwtDuration: Long?): JwtResponse {
        val (details, refreshDetails) = authenticationToJwtDetails(authentication, jwtDuration)
        return JwtResponse(
            successful = true,
            token = jwtUtils.createJWT(details, jwtDuration),
            refreshToken = jwtUtils.createRefreshJWT(refreshDetails)
        )
    }
}