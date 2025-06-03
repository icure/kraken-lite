package org.taktik.icure.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.taktik.icure.properties.AuthProperties

@Component
@Profile("app")
@ConfigurationProperties(prefix = "icure.auth")
class LiteAuthPropertiesImpl(
	override var validationSkewSeconds: Long = 10,
	override var jwt: AuthProperties.Jwt = Jwt(),
	override var knownCloudJwtIssuers: List<String> = listOf("*.icure.cloud"),
	override var allowUnsecureCloudJwtIssuer: Boolean = false,
) : LiteAuthProperties {
	class Jwt(
		override var expirationSeconds: Long = 3600,
		override var refreshExpirationSeconds: Long = 86400,
	) : AuthProperties.Jwt
}

interface LiteAuthProperties : AuthProperties {
	val knownCloudJwtIssuers: List<String>
	/**
	 * For testing purposes, true if http is valid for jwt issuers (else only https is allowed)
	 */
	val allowUnsecureCloudJwtIssuer: Boolean
}