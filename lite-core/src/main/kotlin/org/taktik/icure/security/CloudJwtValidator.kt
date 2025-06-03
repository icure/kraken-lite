package org.taktik.icure.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.icure.asyncjacksonhttpclient.net.web.HttpMethod
import io.icure.asyncjacksonhttpclient.net.web.WebClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.taktik.icure.security.jwt.JwtDecoder
import org.taktik.icure.security.jwt.JwtKeyUtils
import org.taktik.icure.security.jwt.USER_ID
import java.net.URI
import kotlin.text.matches

@Service
@OptIn(ExperimentalCoroutinesApi::class)
class CloudJwtValidator(
	private val liteAuthProperties: LiteAuthProperties,
	private val objectMapper: ObjectMapper,
	private val httpClient: WebClient,
) {
	private val cloudHostPatterns = liteAuthProperties.knownCloudJwtIssuers.map(CloudHostPattern::parse)

	suspend fun validateCloudJwtAndExtractUserId(
		issuer: String,
		cloudToken: String
	): String {
		val issuerUri = URI(issuer)
		require(issuerUri.scheme == "https") {
			"Issuer URI scheme must be https"
		}
		require(issuerUri.host?.let { host -> cloudHostPatterns.any { it.matches(host) } } == true) {
			"Cloud issuer not among known hosts"
		}
		val validationKey = httpClient.uri(URI(issuerUri.scheme, null, issuerUri.host, issuerUri.port, "/rest/v2/auth/publicKey/authJwt", null, null))
			.method(HttpMethod.GET)
			.retrieve()
			.toTextFlow()
			.toList()
			.joinToString()
			.let { JwtKeyUtils.decodePublicKeyFromString(objectMapper.readValue<String>(it)) }
		val claims = JwtDecoder.validateAndGetClaims(cloudToken, validationKey, liteAuthProperties.validationSkewSeconds)
		return claims.getValue(USER_ID) as String
	}
}

private sealed interface CloudHostPattern {
	companion object {
		private val EXACT_REGEX = Regex("[a-z]+(?:\\.[a-z]+)+")
		private val SUB_DOMAIN_REGEX = Regex("\\*\\.[a-z]+(?:\\.[a-z]+)+")

		fun parse(
			pattern: String
		): CloudHostPattern = when {
			pattern.matches(EXACT_REGEX) -> Exact(pattern)
			pattern.matches(SUB_DOMAIN_REGEX) -> SubDomain(pattern.drop(1))
			else -> throw IllegalArgumentException("Invalid cloud issuer pattern $pattern")
		}
	}

	fun matches(host: String): Boolean

	class Exact(val pattern: String) : CloudHostPattern {
		override fun matches(host: String): Boolean {
			return pattern == host
		}
	}

	class SubDomain(val terminator: String) : CloudHostPattern {
		override fun matches(host: String): Boolean {
			return host.endsWith(terminator)
		}
	}
}