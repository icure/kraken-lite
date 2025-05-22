package org.taktik.icure.security

import org.taktik.icure.entities.DataOwnerType
import org.taktik.icure.entities.security.jwt.JwtResponse
import org.taktik.icure.security.jwt.AbstractJwtAuthentication
import org.taktik.icure.security.jwt.BaseJwtDetails
import org.taktik.icure.security.jwt.BaseJwtRefreshDetails
import org.taktik.icure.security.jwt.JwtUtils
import org.taktik.icure.services.external.rest.v1.dto.AuthenticationResponse

class LiteJwtAuthentication(
	authClaims: BaseJwtDetails,
): AbstractJwtAuthentication<BaseJwtDetails>(
	authorities = authClaims.authorities,
	authClaims = authClaims,
	details = emptyMap(),
	authenticated = true
) {
	private fun createRefreshDetails(jwtUtils: JwtUtils, jwtDuration: Long?): BaseJwtRefreshDetails {
		val refreshDetails = BaseJwtRefreshDetails(
			userId = authClaims.userId,
			jwtDuration = jwtDuration ?: jwtUtils.properties.jwt.expirationSeconds,
		)
		return refreshDetails
	}

	@Suppress("DEPRECATION")
	override fun toAuthenticationResponse(jwtUtils: JwtUtils, username: String?, jwtDuration: Long?): AuthenticationResponse {
		val refreshDetails = createRefreshDetails(jwtUtils, jwtDuration)
		return AuthenticationResponse(
			successful = true,
			token = jwtUtils.createAuthJWT(authClaims, jwtDuration),
			refreshToken = jwtUtils.createRefreshJWT(refreshDetails),
			healthcarePartyId = authClaims.dataOwnerId.takeIf { authClaims.dataOwnerType == DataOwnerType.HCP },
			dataOwnerId = authClaims.dataOwnerId,
			groupId = "",
			userId = authClaims.userId,
			username = username
		)
	}

	override fun toJwtResponse(jwtUtils: JwtUtils, jwtDuration: Long?): JwtResponse {
		val refreshDetails = createRefreshDetails(jwtUtils, jwtDuration)
		return JwtResponse(
			successful = true,
			token = jwtUtils.createAuthJWT(authClaims, jwtDuration),
			refreshToken = jwtUtils.createRefreshJWT(refreshDetails)
		)
	}
}