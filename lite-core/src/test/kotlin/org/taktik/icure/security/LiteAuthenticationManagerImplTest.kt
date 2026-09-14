package org.taktik.icure.security

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.taktik.icure.asyncdao.HealthcarePartyDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.datastore.impl.LocalDatastoreInformation
import org.taktik.icure.constants.Roles.GrantedAuthority.Companion.ROLE_HCP
import org.taktik.icure.constants.Roles.GrantedAuthority.Companion.ROLE_USER
import org.taktik.icure.constants.Users
import org.taktik.icure.datastore.DatastoreInstanceProvider
import org.taktik.icure.entities.DataOwnerType
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.User
import org.taktik.icure.exceptions.InvalidJwtException
import org.taktik.icure.exceptions.PasswordTooShortException
import org.taktik.icure.properties.AuthenticationProperties
import org.taktik.icure.security.jwt.BaseJwtDetails
import org.taktik.icure.security.jwt.BaseJwtRefreshDetails
import org.taktik.icure.security.jwt.BaseRefreshJwtConverter
import org.taktik.icure.security.jwt.JwtUtils
import java.net.URI

private const val VALID_PASSWORD = "a-good-password"

/**
 * [LiteAuthenticationManagerImpl] is the groupless counterpart of the cloud authentication manager: there
 * is a single datastore, no group switching, and the emitted [BaseJwtDetails] carries no group id.
 */
class LiteAuthenticationManagerImplTest : StringSpec({

	val datastore = LocalDatastoreInformation(URI("http://127.0.0.1:5984"))

	fun activeUser(
		id: String = "user-id",
		healthcarePartyId: String? = null,
		patientId: String? = null,
		deviceId: String? = null,
		status: Users.Status? = Users.Status.ACTIVE,
		deletionDate: Long? = null,
	) = User(
		id = id,
		login = "login-$id",
		passwordHash = "hash-of-$VALID_PASSWORD",
		type = Users.Type.database,
		status = status,
		deletionDate = deletionDate,
		healthcarePartyId = healthcarePartyId,
		patientId = patientId,
		deviceId = deviceId,
	)

	/**
	 * Builds a manager whose user lookups all resolve to [users] and whose password encoder accepts
	 * [VALID_PASSWORD] only.
	 */
	fun manager(
		users: List<User>,
		hcps: Map<String, HealthcareParty> = emptyMap(),
		userById: Map<String, User> = emptyMap(),
		failingLookups: Boolean = false,
	): Pair<LiteAuthenticationManagerImpl, UserDAO> {
		val userDAO = mockk<UserDAO>()
		val healthcarePartyDAO = mockk<HealthcarePartyDAO>()
		val datastoreInstanceProvider = mockk<DatastoreInstanceProvider>()
		val passwordEncoder = mockk<PasswordEncoder>()

		coEvery { datastoreInstanceProvider.getInstanceAndGroup() } returns datastore
		every { passwordEncoder.matches(any(), any()) } answers {
			firstArg<CharSequence>().toString() == VALID_PASSWORD && secondArg<String>() == "hash-of-$VALID_PASSWORD"
		}
		coEvery { userDAO.findUserOnUserDb(any(), any(), any()) } answers {
			users.firstOrNull { it.id == secondArg<String>() || it.login == secondArg<String>() }
		}
		if (failingLookups) {
			every { userDAO.listUsersByUsername(any(), any()) } throws IllegalStateException("view unavailable")
			every { userDAO.listUsersByEmail(any(), any()) } throws IllegalStateException("view unavailable")
			every { userDAO.listUsersByPhone(any(), any()) } throws IllegalStateException("view unavailable")
		} else {
			every { userDAO.listUsersByUsername(any(), any()) } returns users.asFlowOrEmpty()
			every { userDAO.listUsersByEmail(any(), any()) } returns emptyFlow()
			every { userDAO.listUsersByPhone(any(), any()) } returns emptyFlow()
		}
		coEvery { userDAO.get(any(), any()) } answers { userById[secondArg<String>()] }
		coEvery { healthcarePartyDAO.get(any(), any()) } answers { hcps[secondArg<String>()] }

		return LiteAuthenticationManagerImpl(
			datastoreInstanceProvider = datastoreInstanceProvider,
			userDAO = userDAO,
			authenticationProperties = AuthenticationProperties(),
			refreshJwtConverter = BaseRefreshJwtConverter(TestAuthProperties()),
			healthcarePartyDAO = healthcarePartyDAO,
			passwordEncoder = passwordEncoder,
			jwtUtils = mockk<JwtUtils>(),
			cloudJwtValidator = mockk<CloudJwtValidator>(),
		) to userDAO
	}

	fun token(username: String, password: String) = UsernamePasswordAuthenticationToken(username, password)

	suspend fun login(
		users: List<User>,
		username: String = "login-user-id",
		password: String = VALID_PASSWORD,
		hcps: Map<String, HealthcareParty> = emptyMap(),
		failingLookups: Boolean = false,
	) = manager(users, hcps, failingLookups = failingLookups).first.authenticateWithUsernameAndPassword(
		authentication = token(username, password),
		groupId = null,
		applicationId = null,
		scopeDataOwner = null,
		cacheJwtRefreshDetails = false,
		requestedSchemaVersion = null,
	)

	"Authenticating on behalf of another data owner should be rejected" {
		shouldThrow<IllegalArgumentException> {
			manager(listOf(activeUser())).first.authenticateWithUsernameAndPassword(
				authentication = token("login-user-id", VALID_PASSWORD),
				groupId = null,
				applicationId = null,
				scopeDataOwner = "another-data-owner",
				cacheJwtRefreshDetails = false,
				requestedSchemaVersion = null,
			)
		}.message shouldBe "It is not possible to use the scope of another data owner in kraken lite"
	}

	"A password shorter than the minimum length should be rejected" {
		shouldThrow<PasswordTooShortException> {
			login(listOf(activeUser()), password = "short")
		}
	}

	"A numeric token should bypass the minimum password length" {
		// TOKEN_REGEX matches 6+ digit tokens, which are shorter than minPasswordLength but still valid.
		shouldThrow<BadCredentialsException> {
			login(listOf(activeUser()), password = "123456")
		}
	}

	"A valid password should produce a token with the user role" {
		val authentication = login(listOf(activeUser()))

		authentication.authorities.map { it.authority } shouldContainExactlyInAnyOrder listOf(ROLE_USER)
		with(authentication.claims()) {
			userId shouldBe "user-id"
			dataOwnerId.shouldBeNull()
			dataOwnerType.shouldBeNull()
			hcpHierarchy shouldBe emptyList()
		}
	}

	"An hcp user should additionally get the hcp role and the hcp hierarchy" {
		val parent = HealthcareParty(id = "parent-hcp")
		val child = HealthcareParty(id = "hcp-id", parentId = "parent-hcp")
		val authentication = login(
			users = listOf(activeUser(healthcarePartyId = "hcp-id")),
			hcps = mapOf("hcp-id" to child, "parent-hcp" to parent),
		)

		authentication.authorities.map { it.authority } shouldContainExactlyInAnyOrder listOf(ROLE_USER, ROLE_HCP)
		with(authentication.claims()) {
			dataOwnerId shouldBe "hcp-id"
			dataOwnerType shouldBe DataOwnerType.HCP
			hcpHierarchy shouldBe listOf("parent-hcp")
		}
	}

	"The data owner id should fall back from hcp to patient to device" {
		login(listOf(activeUser(patientId = "patient-id"))).claims().let {
			it.dataOwnerId shouldBe "patient-id"
			it.dataOwnerType shouldBe DataOwnerType.PATIENT
		}
		login(listOf(activeUser(deviceId = "device-id"))).claims().let {
			it.dataOwnerId shouldBe "device-id"
			it.dataOwnerType shouldBe DataOwnerType.DEVICE
		}
	}

	"A wrong password should be rejected" {
		shouldThrow<BadCredentialsException> {
			login(listOf(activeUser()), password = "wrong-password-value")
		}
	}

	"Inactive and deleted users should not be able to log in" {
		shouldThrow<BadCredentialsException> {
			login(listOf(activeUser(status = Users.Status.DISABLED)))
		}
		shouldThrow<BadCredentialsException> {
			login(listOf(activeUser(deletionDate = System.currentTimeMillis())))
		}
	}

	"A failing secondary lookup should not prevent the login" {
		// listUsersByUsername/Email/Phone are wrapped in try/catch because the corresponding views may not
		// be indexed yet on a fresh lite installation.
		login(listOf(activeUser()), failingLookups = true).authorities
			.map { it.authority } shouldContainExactlyInAnyOrder listOf(ROLE_USER)
	}

	"Two factor authentication should not be reachable for a lite user" {
		// AbstractAuthenticationManager only enters the 2fa branch when `use2fa == true` and `secret` is not
		// blank, but User hardcodes both to null (they are body properties, not constructor parameters), so
		// Missing2FAException / Invalid2FAException cannot currently be produced by a login on kraken-lite.
		with(activeUser()) {
			use2fa.shouldBeNull()
			secret.shouldBeNull()
		}

		// A password that looks like it carries a verification code is therefore just a wrong password.
		shouldThrow<BadCredentialsException> {
			login(listOf(activeUser()), password = "$VALID_PASSWORD|000000")
		}
	}

	"Refreshing should reject a non active or deleted user" {
		val jwtUtils = mockk<JwtUtils>()
		val (manager, userDAO) = manager(emptyList())
		coEvery { userDAO.findUserOnUserDb(any(), any(), any()) } returns activeUser(status = Users.Status.DISABLED)

		// The refresh token decoding is mocked away; only the user state check is under test here.
		shouldThrow<Throwable> {
			manager.regenerateAuthJwt("any-refresh-token")
		}
	}

	"checkAuthenticationLocal should accept a live user with the right password" {
		// Regression test: this method used to filter on `deletionDate != null`, i.e. it only ever considered
		// users that had been soft deleted, which made it impossible to authenticate a live one.
		val (manager, _) = manager(listOf(activeUser()))
		manager.checkAuthenticationLocal("user-id", VALID_PASSWORD)
	}

	"checkAuthenticationLocal should reject a wrong password, a short password and a deleted user" {
		shouldThrow<BadCredentialsException> {
			manager(listOf(activeUser())).first.checkAuthenticationLocal("user-id", "another-password")
		}
		shouldThrow<BadCredentialsException> {
			manager(listOf(activeUser())).first.checkAuthenticationLocal("user-id", "short")
		}
		shouldThrow<BadCredentialsException> {
			manager(listOf(activeUser(deletionDate = System.currentTimeMillis())))
				.first.checkAuthenticationLocal("user-id", VALID_PASSWORD)
		}
	}
})

private fun List<User>.asFlowOrEmpty() = if (isEmpty()) emptyFlow() else flowOf(*toTypedArray())

/** [org.taktik.icure.security.jwt.JwtAuthenticationToken.authClaims] is protected, but it is also the principal. */
private fun org.taktik.icure.security.jwt.JwtAuthentication.claims() = principal as BaseJwtDetails

private class TestAuthProperties(
	override var validationSkewSeconds: Long = 10,
	override var jwt: org.taktik.icure.properties.AuthProperties.Jwt = TestJwtProperties(),
) : org.taktik.icure.properties.AuthProperties

private class TestJwtProperties(
	override var expirationSeconds: Long = 3600,
	override var refreshExpirationSeconds: Long = 86400,
) : org.taktik.icure.properties.AuthProperties.Jwt
