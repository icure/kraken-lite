package org.taktik.icure.services.external.rest.lite.v1.controllers.support

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.asyncservice.UserService
import org.taktik.icure.properties.GroupProperties
import org.taktik.icure.services.external.rest.v1.dto.UserGroupDto
import org.taktik.icure.utils.injectReactorContext

@RestController("userLiteController")
@Profile("app")
@RequestMapping("/rest/v1/user")
@Tag(name = "user") // otherwise would default to "user-controller"
class UserController (
	private val userService: UserService,
	private val sessionInfo: SessionInformationProvider,
	private val groupProperties: GroupProperties
) {
	companion object {
		private val logger = LoggerFactory.getLogger(this::class.java)
	}

	@Operation(summary = "Get presently logged-in user.", description = "Get current user.")
	@GetMapping("/matches")
	fun getMatchingUsers() = flow {
		if (groupProperties.id != null) {
			val user = userService.getUser(sessionInfo.getCurrentUserId(), false)
			emit(UserGroupDto(
				groupId = groupProperties.id,
				groupName = groupProperties.name,
				groupsHierarchy = emptyList(),
				userId = user?.id,
				login = user?.login,
				name = user?.name,
				email = user?.email,
				phone = user?.mobilePhone,
				patientId = user?.patientId,
				healthcarePartyId = user?.healthcarePartyId,
				deviceId = user?.deviceId,
				nameOfParentOfTopmostGroupInHierarchy = null
			))
		} else logger.warn("No group id configured, returning empty list of matching users.")
	}.injectReactorContext()
}
