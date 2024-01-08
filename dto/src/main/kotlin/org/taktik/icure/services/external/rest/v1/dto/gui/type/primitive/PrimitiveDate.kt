/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.rest.v1.dto.gui.type.primitive

import java.io.Serializable
import java.util.Date
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.services.external.rest.v1.dto.gui.type.Data

/**
 * Created by aduchate on 19/11/13, 10:14
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class PrimitiveDate(val value: Date? = null) : Data(), Primitive {
	override fun getPrimitiveValue(): Serializable? {
		return value
	}
}
