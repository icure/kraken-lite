package org.taktik.icure.security

import io.icure.asyncjacksonhttpclient.net.web.WebClient
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.mockk
import org.taktik.icure.properties.AuthProperties
import org.taktik.icure.security.jwt.JwtDecoder

/**
 * [CloudJwtValidator] guards the `POST /rest/v2/auth/login/icureCloud` endpoint, which is permit-all in
 * [org.taktik.icure.config.SecurityConfigAdapter]. The issuer checks below all happen *before* any
 * network call, so a rejected issuer must never reach the (unmockable here) http client - which is
 * exactly what these tests assert by leaving the client a bare mock that would fail if it were used.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class CloudJwtValidatorTest : StringSpec({

	fun validator(
		knownIssuers: List<String> = listOf("*.icure.cloud"),
		allowUnsecure: Boolean = false,
	) = CloudJwtValidator(
		liteAuthProperties = TestLiteAuthProperties(
			knownCloudJwtIssuers = knownIssuers,
			allowUnsecureCloudJwtIssuer = allowUnsecure,
		),
		httpClient = mockk<WebClient>(),
		jwtDecoder = mockk<JwtDecoder>(),
	)

	suspend fun rejectionMessage(
		issuer: String,
		knownIssuers: List<String> = listOf("*.icure.cloud"),
		allowUnsecure: Boolean = false,
	): String = shouldThrow<IllegalArgumentException> {
		validator(knownIssuers, allowUnsecure).validateCloudJwtAndExtractUserId(issuer, "any-token")
	}.message ?: ""

	"A subdomain pattern should match any host under it" {
		listOf(
			"https://api.icure.cloud",
			"https://nested.api.icure.cloud",
			"https://api.icure.cloud:8443",
		).forEach { issuer ->
			// The host is accepted, so validation proceeds past the host check and only then fails on the
			// http client mock - i.e. it does not fail with "not among known hosts".
			runCatching {
				validator().validateCloudJwtAndExtractUserId(issuer, "any-token")
			}.exceptionOrNull()?.message?.contains("not among known hosts") shouldBe false
		}
	}

	"A subdomain pattern should not match the bare domain nor a lookalike" {
		// "*.icure.cloud" is parsed as the terminator ".icure.cloud", so the apex domain does not match.
		rejectionMessage("https://icure.cloud") shouldContain "not among known hosts"
		rejectionMessage("https://evil-icure.cloud") shouldContain "not among known hosts"
		rejectionMessage("https://icure.cloud.evil.com") shouldContain "not among known hosts"
	}

	"An exact pattern should match only that host" {
		val known = listOf("icure.cloud")
		runCatching {
			validator(known).validateCloudJwtAndExtractUserId("https://icure.cloud", "any-token")
		}.exceptionOrNull()?.message?.contains("not among known hosts") shouldBe false

		rejectionMessage("https://api.icure.cloud", known) shouldContain "not among known hosts"
		// A suffix match must not be enough for an exact pattern.
		rejectionMessage("https://evil-icure.cloud", known) shouldContain "not among known hosts"
	}

	"An invalid issuer pattern should be rejected when the validator is built" {
		listOf("", "icure", "*icure.cloud", "*.icure", "not a host", "*.*.icure.cloud").forEach { pattern ->
			shouldThrow<IllegalArgumentException> {
				validator(knownIssuers = listOf(pattern))
			}.message shouldContain "Invalid cloud issuer pattern"
		}
	}

	"An http issuer should be rejected unless unsecure issuers are explicitly allowed" {
		rejectionMessage("http://api.icure.cloud") shouldContain "scheme must be https"

		// With allowUnsecureCloudJwtIssuer the scheme check passes and validation moves on.
		runCatching {
			validator(allowUnsecure = true).validateCloudJwtAndExtractUserId("http://api.icure.cloud", "any-token")
		}.exceptionOrNull()?.message?.contains("scheme must be https") shouldBe false
	}

	"A non http scheme should always be rejected" {
		listOf("ftp://api.icure.cloud", "file:///etc/passwd").forEach { issuer ->
			rejectionMessage(issuer, allowUnsecure = true) shouldContain "scheme must be https"
		}
	}

	"An issuer without a host should be rejected" {
		rejectionMessage("https:///no-host") shouldContain "not among known hosts"
	}

	"Several patterns should be accepted together" {
		val known = listOf("icure.cloud", "*.icure.dev")
		listOf("https://icure.cloud", "https://api.icure.dev").forEach { issuer ->
			runCatching {
				validator(known).validateCloudJwtAndExtractUserId(issuer, "any-token")
			}.exceptionOrNull()?.message?.contains("not among known hosts") shouldBe false
		}
		rejectionMessage("https://api.icure.cloud", known) shouldContain "not among known hosts"
	}
})

private class TestLiteAuthProperties(
	override val knownCloudJwtIssuers: List<String>,
	override val allowUnsecureCloudJwtIssuer: Boolean,
	override var validationSkewSeconds: Long = 10,
	override var jwt: AuthProperties.Jwt = TestJwt(),
) : LiteAuthProperties

private class TestJwt(
	override var expirationSeconds: Long = 3600,
	override var refreshExpirationSeconds: Long = 86400,
) : AuthProperties.Jwt
