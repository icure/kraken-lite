package org.taktik.icure.services.external.rest.lite.v2.controllers.support

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.reactor.mono
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.taktik.icure.security.LiteAuthenticationManager

@RestController("loginLiteControllerV2")
@Profile("app")
@RequestMapping("/rest/v2/auth")
@Tag(name = "auth")
class LoginController (
	private val authenticationManager: LiteAuthenticationManager,
) {
	@Operation(summary = "login", description = "Login using username and password")
	@PostMapping("/login/icureCloud")
	fun login(
		@RequestParam(required = true) issuer: String,
		@RequestHeader(name = "Cloud-Auth-Token") cloudToken: String,
	) = mono {
		authenticationManager.loginWithCloudJwt(issuer, cloudToken)
	}
}