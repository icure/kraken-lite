/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.rest.v1.dto

@Deprecated("Use JwtResponse instead")
class AuthenticationResponse(
	val groupId: String? = null,
	val userId: String? = null,
	val dataOwnerId: String? = null,
	val healthcarePartyId: String? = null,
	val reason: String? = null,
	val successful: Boolean = false,
	val username: String? = null,
	val token: String? = null,
	val refreshToken: String? = null,
)
