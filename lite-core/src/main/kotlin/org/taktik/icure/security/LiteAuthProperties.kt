package org.taktik.icure.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.taktik.icure.properties.AuthProperties

@Component
@Profile("app")
@ConfigurationProperties(prefix = "icure.auth")
class LiteAuthProperties(
	override var validationSkewSeconds: Long = 10,
	override var jwt: AuthProperties.Jwt = Jwt(),
) : AuthProperties {
	class Jwt(
		override var expirationSeconds: Long = 3600,
		override var refreshExpirationSeconds: Long = 86400,
	) : AuthProperties.Jwt

}