/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.utils

import java.io.IOException
import java.math.BigDecimal
import java.time.Instant
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class InstantDeserializer : JsonDeserializer<Instant>() {
	@Throws(IOException::class)
	override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Instant? {
		return when {
			jp.currentToken.isNumeric -> jp.decimalValue
			jp.currentToken.isScalarValue -> BigDecimal(jp.valueAsString)
			else -> null
		}?.let { getInstant(it) }
	}

	private fun getInstant(decVal: BigDecimal): Instant {
		return Instant.ofEpochSecond(decVal.divide(_1000).toLong(), decVal.remainder(_1000).multiply(_1000000).toLong())
	}

	companion object {
		private val _1000000 = BigDecimal.valueOf(1000000)
		private val _1000 = BigDecimal.valueOf(1000)
	}
}
