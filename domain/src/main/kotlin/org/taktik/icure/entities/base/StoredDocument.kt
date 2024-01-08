/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.entities.base

import com.fasterxml.jackson.annotation.JsonProperty
import org.taktik.couchdb.entity.Attachment
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.couchdb.entity.Versionable
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.exceptions.DeserializationTypeException

interface StoredDocument : Versionable<String> {
	@Suppress("PropertyName")
	@JsonProperty("java_type")
	fun getJavaType(): String {
		return this::class.qualifiedName!!
	}
	@JsonProperty("java_type")
	fun setJavaType(value: String) {
		if (this::class.qualifiedName != value) throw DeserializationTypeException(
			this.id,
			this::class,
			value
		)
	}

	val deletionDate: Long?
	val revisionsInfo: List<RevisionInfo>?
	val conflicts: List<String>?
	val attachments: Map<String, Attachment>?

	fun solveConflictsWith(other: StoredDocument): Map<String, Any?> {
		return mapOf(
			"id" to this.id,
			"rev" to this.rev,
			"revHistory" to (other.revHistory?.let { it + (this.revHistory ?: mapOf()) } ?: this.revHistory),
			"revisionsInfo" to this.revisionsInfo,
			"conflicts" to this.conflicts,
			"attachments" to solveAttachmentsConflicts(this.attachments, other.attachments),
			"deletionDate" to (this.deletionDate ?: other.deletionDate)
		)
	}

	private fun solveAttachmentsConflicts(thisAttachments: Map<String, Attachment>?, otherAttachments: Map<String, Attachment>?): Map<String, Attachment>? = this.attachments?.mapValues {(key, a) ->
		val b = otherAttachments?.get(key)

		if (b != null) {
			if (b.length?.let { it > (a.length ?: 0) } == true) b else a
		} else a
	}

	fun withDeletionDate(deletionDate: Long?): StoredDocument
}

fun Versionable<String>.idAndRev(): IdAndRev = IdAndRev(this.id, this.rev)