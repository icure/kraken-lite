/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.rest.v1.dto

import java.io.Serializable
import java.util.Date

class TaskExecutionErrorDto : Serializable {
	var date: Date? = null
	var error: String? = null

	companion object {
		private const val serialVersionUID = 1L
	}
}
