/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.entities.embed

import java.io.Serializable
import org.taktik.icure.entities.base.CodeStub

data class Weekday(
	val weekday: CodeStub? = null, //CD-WEEKDAY
	val weekNumber: Int? = null //Can be null
) : Serializable
