package org.taktik.icure.entities.utils

import com.fasterxml.jackson.annotation.JsonProperty
import org.taktik.couchdb.entity.Attachment
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo

/**
 * Represents a constant for an icure-oss installation. This constant may be randomly generated the first time it is used,
 * but then stays constant, even after a system restart.
 * Note that the installation constant id is not generated randomly every time, to ensure uniqueness of constants.
 * @param value string representation of the value of the constant.
 */
data class InstallationConstant(
	@JsonProperty("_id") override val id: String,
	@JsonProperty("_rev") override val rev: String? = null,
	@JsonProperty("deleted") override val deletionDate: Long? = null,
	@JsonProperty("rev_history") override val revHistory: Map<String, String>? = null,
	@JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
	@JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
	@JsonProperty("_conflicts") override val conflicts: List<String>? = null,
	val value: String? = null
) : StoredDocument {
	override fun withIdRev(id: String?, rev: String): InstallationConstant =
		id?.let { this.copy(id = it, rev = rev) } ?: this.copy(rev = rev)

	override fun withDeletionDate(deletionDate: Long?): StoredDocument =
		this.copy(deletionDate = deletionDate)
}
