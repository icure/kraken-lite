/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.rest.v1.dto

import java.io.Serializable

class CreateMikronoAccountReply : Serializable {
	var mikronoUrl: String? = null
	var oauthUrl: String? = null
	var sessionId: String? = null

	companion object {
		/**
		 *
		 */
		private const val serialVersionUID = 1L
	}
}
