package org.taktik.icure.security

import io.icure.asyncjacksonhttpclient.net.web.HttpMethod
import io.icure.asyncjacksonhttpclient.net.web.WebClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.taktik.icure.security.jwt.JwtDecoder
import org.taktik.icure.security.jwt.JwtKeyUtils
import org.taktik.icure.security.jwt.USER_ID
import java.net.URI
import java.net.URISyntaxException
import kotlin.text.matches

@Service
@OptIn(ExperimentalCoroutinesApi::class)
class CloudJwtValidator(
	private val liteAuthProperties: LiteAuthProperties,
	private val httpClient: WebClient,
) {
	private val cloudHostPatterns = liteAuthProperties.knownCloudJwtIssuers.map(CloudHostPattern::parse)

	suspend fun validateCloudJwtAndExtractUserId(
		issuer: String,
		cloudToken: String
	): String {
		val issuerUri = try {
			URI(issuer)
		} catch (e: URISyntaxException) {
			throw IllegalArgumentException("Issuer $issuer is not a valid uri", e)
		}
		require(issuerUri.scheme == "https" || (liteAuthProperties.allowUnsecureCloudJwtIssuer && issuerUri.scheme == "http")) {
			"Issuer URI scheme must be https or, if unsecure issuers are allowed, http"
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
			.let { JwtKeyUtils.decodePublicKeyFromString(it) }
		val claims = JwtDecoder.validateAndGetClaims(cloudToken, validationKey, liteAuthProperties.validationSkewSeconds)
		return claims.getValue(USER_ID) as String
	}
}

private sealed interface CloudHostPattern {
	companion object {
		private val URL_CHAR = "[0-9A-Za-z_.~\\-]"
		private val EXACT_REGEX = Regex("$URL_CHAR+(?:\\.$URL_CHAR+)+")
		private val SUB_DOMAIN_REGEX = Regex("\\*\\.$URL_CHAR+(?:\\.$URL_CHAR+)+")

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