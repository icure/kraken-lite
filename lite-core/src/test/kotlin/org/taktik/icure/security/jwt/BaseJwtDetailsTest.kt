package org.taktik.icure.security.jwt

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.taktik.icure.constants.Roles.GrantedAuthority.Companion.ROLE_HCP
import org.taktik.icure.constants.Roles.GrantedAuthority.Companion.ROLE_USER
import org.taktik.icure.entities.DataOwnerType
import java.time.Instant

/**
 * [BaseJwtDetails] is the lite-only JWT payload: unlike the cloud one it carries no group. These tests
 * pin the claim round-trip, since a mismatch between [BaseJwtDetails.toClaimsOmittingExpiration] and
 * [BaseJwtDetails.fromClaims] would silently drop authorities or the data owner from every token.
 */
class BaseJwtDetailsTest : StringSpec({

	val expiration = Instant.ofEpochSecond(1_700_000_000)

	fun details(
		userId: String = "user-id",
		dataOwnerId: String? = "hcp-id",
		dataOwnerType: DataOwnerType? = DataOwnerType.HCP,
		hcpHierarchy: List<String> = listOf("parent-hcp"),
		authorities: Set<GrantedAuthority> = setOf(SimpleGrantedAuthority(ROLE_USER), SimpleGrantedAuthority(ROLE_HCP)),
	) = BaseJwtDetails(
		userId = userId,
		dataOwnerId = dataOwnerId,
		dataOwnerType = dataOwnerType,
		hcpHierarchy = hcpHierarchy,
		authorities = authorities,
	)

	"Claims should round trip through fromClaims" {
		val original = details()
		val claims = original.serializedClaims() + (Jwt.StandardClaims.EXPIRES_AT to expiration)

		with(BaseJwtDetails.fromClaims(claims)) {
			userId shouldBe original.userId
			dataOwnerId shouldBe original.dataOwnerId
			dataOwnerType shouldBe DataOwnerType.HCP
			hcpHierarchy shouldBe original.hcpHierarchy
			authorities.map { it.authority } shouldContainExactlyInAnyOrder listOf(ROLE_USER, ROLE_HCP)
			this.expiration shouldBe expiration.epochSecond
		}
	}

	"Null values should be omitted from the claims" {
		val claims = details(dataOwnerId = null, dataOwnerType = null).toClaimsOmittingExpiration()

		claims.containsKey(DATA_OWNER_ID) shouldBe false
		claims.containsKey(DATA_OWNER_TYPE) shouldBe false
		claims.containsKey(USER_ID) shouldBe true
		// The expiration is deliberately not part of this map - it is added by the signing code.
		claims.containsKey(Jwt.StandardClaims.EXPIRES_AT) shouldBe false
	}

	"A missing data owner should decode back to null" {
		val claims = details(dataOwnerId = null, dataOwnerType = null).toClaimsOmittingExpiration() +
			(Jwt.StandardClaims.EXPIRES_AT to expiration)

		with(BaseJwtDetails.fromClaims(claims)) {
			dataOwnerId.shouldBeNull()
			dataOwnerType.shouldBeNull()
		}
	}

	"The data owner type should be parsed case insensitively" {
		listOf("hcp" to DataOwnerType.HCP, "HCP" to DataOwnerType.HCP, "Patient" to DataOwnerType.PATIENT, "device" to DataOwnerType.DEVICE)
			.forEach { (raw, expected) ->
				BaseJwtDetails.fromClaims(
					mapOf(
						USER_ID to "user-id",
						DATA_OWNER_TYPE to raw,
						AUTHORITIES to listOf(ROLE_USER),
						Jwt.StandardClaims.EXPIRES_AT to expiration,
					),
				).dataOwnerType shouldBe expected
			}
	}

	"An unknown data owner type should decode to null rather than throw" {
		BaseJwtDetails.fromClaims(
			mapOf(
				USER_ID to "user-id",
				DATA_OWNER_TYPE to "something-else",
				AUTHORITIES to listOf(ROLE_USER),
				Jwt.StandardClaims.EXPIRES_AT to expiration,
			),
		).dataOwnerType.shouldBeNull()
	}

	"An absent hcp hierarchy claim should decode to an empty list" {
		BaseJwtDetails.fromClaims(
			mapOf(
				USER_ID to "user-id",
				AUTHORITIES to listOf(ROLE_USER),
				Jwt.StandardClaims.EXPIRES_AT to expiration,
			),
		).hcpHierarchy shouldBe emptyList()
	}

	"Non string entries in the claims collections should be ignored" {
		with(
			BaseJwtDetails.fromClaims(
				mapOf(
					USER_ID to "user-id",
					HCP_HIERARCHY to listOf("a", 42, null, "b"),
					AUTHORITIES to listOf(ROLE_USER, 1),
					Jwt.StandardClaims.EXPIRES_AT to expiration,
				),
			),
		) {
			hcpHierarchy shouldBe listOf("a", "b")
			authorities.map { it.authority } shouldBe listOf(ROLE_USER)
		}
	}

	"An empty hcp hierarchy should round trip" {
		val claims = details(hcpHierarchy = emptyList()).serializedClaims() +
			(Jwt.StandardClaims.EXPIRES_AT to expiration)
		BaseJwtDetails.fromClaims(claims).hcpHierarchy shouldBe emptyList()
	}

	"The data owner type claim should be written as an enum and read back as a string" {
		// toClaimsOmittingExpiration puts the DataOwnerType enum in the map, while fromClaims casts the
		// claim to String. The two are only compatible because JwtEncoder hands the value to nimbus, which
		// serializes it with toString() when the token is written, so it comes back as a string on decode.
		// Pinned here because a direct in-memory round trip of these two functions does NOT work.
		details().toClaimsOmittingExpiration()[DATA_OWNER_TYPE] shouldBe DataOwnerType.HCP

		shouldThrow<ClassCastException> {
			BaseJwtDetails.fromClaims(
				details().toClaimsOmittingExpiration() + (Jwt.StandardClaims.EXPIRES_AT to expiration),
			)
		}
	}
})

/**
 * Reproduces what the token actually carries: [org.taktik.icure.security.jwt.JwtEncoder] feeds every claim
 * to nimbus, which writes non-primitive values such as the [DataOwnerType] enum using `toString()`.
 */
private fun BaseJwtDetails.serializedClaims(): Map<String, Any?> =
	toClaimsOmittingExpiration().mapValues { (_, value) ->
		if (value is Enum<*>) value.toString() else value
	}
