/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.entities.embed

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Right(
	val userId: String? = null,
	val read: Boolean = false,
	val write: Boolean = false,
	val administration: Boolean = false
) : Serializable
