/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.handlers

import java.lang.IllegalArgumentException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import org.taktik.icure.domain.ContactIdServiceId

class JacksonContactIdServiceIdDeserializer : JsonDeserializer<ContactIdServiceId>() {
	override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ContactIdServiceId {
		return if (p.currentToken()?.isScalarValue == true) {
			ContactIdServiceId(p.readValueAs(String::class.java))
		} else {
			p.readValueAs(HashMap::class.java).let {
				ContactIdServiceId(contactId = it["contactId"] as? String ?: throw IllegalArgumentException("Missing contactId"), serviceId = it["serviceId"] as? String, modified = it["modified"] as? Long)
			}
		}
	}
}
