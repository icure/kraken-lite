package org.taktik.icure.security.jwt

import io.jsonwebtoken.Claims
import org.taktik.icure.exceptions.InvalidJwtException

data class BaseJwtRefreshDetails(
    override val userId: String,
    override val jwtDuration: Long
) : JwtRefreshDetails {

    companion object : JwtConverter<JwtRefreshDetails> {

        override fun fromClaims(claims: Claims, jwtExpirationTime: Long): JwtRefreshDetails =
            BaseJwtRefreshDetails(
                userId = (claims[USER_ID] as String?) ?: throw InvalidJwtException("Missing user id from claims"),
                jwtDuration = (claims[JWT_DURATION] as? Int?)?.toLong() ?: jwtExpirationTime
            )

    }

    override fun toClaims(): Map<String, Any?> = mapOf(
        USER_ID to userId,
        JWT_DURATION to jwtDuration,
    )

}