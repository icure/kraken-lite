/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.entities.embed

import java.io.Serializable
import java.time.Instant
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.taktik.icure.annotations.entities.ContentValue
import org.taktik.icure.annotations.entities.ContentValues
import org.taktik.icure.utils.InstantDeserializer
import org.taktik.icure.utils.InstantSerializer

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Content(
	@param:ContentValue(ContentValues.ANY_STRING) @JsonProperty("s") val stringValue: String? = null,
	@param:ContentValue(ContentValues.ANY_DOUBLE) @JsonProperty("n") val numberValue: Double? = null,
	@param:ContentValue(ContentValues.ANY_BOOLEAN) @JsonProperty("b") val booleanValue: Boolean? = null,
	@JsonProperty("i")
	@JsonSerialize(using = InstantSerializer::class)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonDeserialize(using = InstantDeserializer::class)
	val instantValue: Instant? = null,
	@param:ContentValue(ContentValues.FUZZY_DATE) @JsonProperty("dt") val fuzzyDateValue: Long? = null,
	@JsonProperty("x") val binaryValue: ByteArray? = null,
	@JsonProperty("d") val documentId: String? = null,
	@param:ContentValue(ContentValues.NESTED_ENTITY) @JsonProperty("m") val measureValue: Measure? = null,
	@param:ContentValue(ContentValues.NESTED_ENTITY) @JsonProperty("p") val medicationValue: Medication? = null,
	@param:ContentValue(ContentValues.NESTED_ENTITIES_SET) @JsonProperty("c") val compoundValue: Set<Service>? = null,
	@param:ContentValue(ContentValues.NESTED_ENTITY) @JsonProperty("ts") val timeSeries: TimeSeries? = null,
	@param:ContentValue(ContentValues.NESTED_ENTITIES_LIST) val ratio: List<Measure>? = null,
	@param:ContentValue(ContentValues.NESTED_ENTITIES_LIST) val range: List<Measure>? = null
) : Serializable {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Content) return false

		if (stringValue != other.stringValue) return false
		if (numberValue != other.numberValue) return false
		if (booleanValue != other.booleanValue) return false
		if (instantValue != other.instantValue) return false
		if (fuzzyDateValue != other.fuzzyDateValue) return false
		if (binaryValue != null) {
			if (other.binaryValue == null) return false
			if (!binaryValue.contentEquals(other.binaryValue)) return false
		} else if (other.binaryValue != null) return false
		if (documentId != other.documentId) return false
		if (measureValue != other.measureValue) return false
		if (medicationValue != other.medicationValue) return false
		if (compoundValue != other.compoundValue) return false
		if (timeSeries != other.timeSeries) return false

		if (ratio != other.ratio) return false
		if (range != other.range) return false

		return true
	}

	override fun hashCode(): Int {
		var result = stringValue?.hashCode() ?: 0
		result = 31 * result + (numberValue?.hashCode() ?: 0)
		result = 31 * result + (booleanValue?.hashCode() ?: 0)
		result = 31 * result + (instantValue?.hashCode() ?: 0)
		result = 31 * result + (fuzzyDateValue?.hashCode() ?: 0)
		result = 31 * result + (binaryValue?.contentHashCode() ?: 0)
		result = 31 * result + (documentId?.hashCode() ?: 0)
		result = 31 * result + (measureValue?.hashCode() ?: 0)
		result = 31 * result + (medicationValue?.hashCode() ?: 0)
		result = 31 * result + (compoundValue?.hashCode() ?: 0)
		result = 31 * result + (ratio?.hashCode() ?: 0)
		result = 31 * result + (range?.hashCode() ?: 0)
		return result
	}
}
