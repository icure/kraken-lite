package org.taktik.icure.security.jwt

import org.springframework.stereotype.Component
import org.taktik.icure.exceptions.InvalidJwtException
import org.taktik.icure.properties.AuthProperties
import java.time.Instant

@Component
class BaseRefreshJwtConverter(
    private val authProperties: AuthProperties
) : JwtConverter<JwtRefreshDetails> {

    override fun fromClaims(claims: Map<String, Any?>): JwtRefreshDetails =
        BaseJwtRefreshDetails(
            userId = (claims[USER_ID] as String?) ?: throw InvalidJwtException("Missing user id from claims"),
            jwtDuration = (claims[JWT_DURATION] as? Int?)?.toLong() ?: authProperties.jwt.refreshExpirationSeconds,
            expiration = (claims[Jwt.StandardClaims.EXPIRES_AT] as Instant).epochSecond
        )

}

data class BaseJwtRefreshDetails(
    override val userId: String,
    override val jwtDuration: Long,
    override val expiration: Long? = null
) : JwtRefreshDetails {

    override fun toClaimsOmittingExpiration(): Map<String, Any?> = mapOf(USER_ID to userId, JWT_DURATION to jwtDuration)

}