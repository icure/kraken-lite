package org.taktik.icure.services.external.rest.v2.dto.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.services.external.rest.v2.dto.base.CodeStubDto
import org.taktik.icure.services.external.rest.v2.dto.base.IdentifiableDto
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(
	description = """Text node with attribution. Could be written by a healthcare party, as a side node of a
    |medical record. For example, after taking a temperature, the HCP adds a node explaining the
    |thermometer is faulty."""
)
data class AnnotationDto(
	@Schema(description = "The Id of the Annotation. We encourage using either a v4 UUID or a HL7 Id.") override val id: String = UUID.randomUUID().toString(),
	val author: String? = null,
	@get:Schema(description = "The timestamp (unix epoch in ms) of creation of this note, will be filled automatically if missing. Not enforced by the application server.") val created: Long? = null,
	@get:Schema(description = "The timestamp (unix epoch in ms) of the latest modification of this note, will be filled automatically if missing. Not enforced by the application server.") val modified: Long? = null,
	@get:Schema(description = "Text contained in the note, written as markdown.", deprecated = true) val text: String? = null,
	@get:Schema(description = "Localized text contained in the note, written as markdown. Keys should respect ISO 639-1") val markdown: Map<String, String> = emptyMap(),
	@get:Schema(description = "Defines to which part of the corresponding information the note is related to.") val location: String? = null,
	val confidential: Boolean? = null,
	val tags: Set<CodeStubDto> = emptySet(),
	val encryptedSelf: String? = null,
	) : IdentifiableDto<String> {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as AnnotationDto

		if (id != other.id) return false
		if (author != other.author) return false
		if (created != other.created) return false
		if (modified != other.modified) return false
		if (text != other.text) return false
		if (location != other.location) return false
		if (confidential != other.confidential) return false
		if (tags != other.tags) return false
		if (encryptedSelf != other.encryptedSelf) return false
		if (markdown != other.markdown) return false

		return true
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + (author?.hashCode() ?: 0)
		result = 31 * result + (created?.hashCode() ?: 0)
		result = 31 * result + (modified?.hashCode() ?: 0)
		result = 31 * result + (text?.hashCode() ?: 0)
		result = 31 * result + (location?.hashCode() ?: 0)
		result = 31 * result + (confidential?.hashCode() ?: 0)
		result = 31 * result + tags.hashCode()
		result = 31 * result + (encryptedSelf?.hashCode() ?: 0)
		result = 31 * result + markdown.hashCode()

		return result
	}
}
