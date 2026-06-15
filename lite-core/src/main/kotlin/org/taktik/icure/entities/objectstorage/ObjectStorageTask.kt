package org.taktik.icure.entities.objectstorage

import java.util.UUID
import com.fasterxml.jackson.annotation.JsonProperty
import org.taktik.couchdb.entity.Attachment
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo

data class ObjectStorageTask(
	@param:JsonProperty("_id") override val id: String,
	@param:JsonProperty("_rev") override val rev: String? = null,
	@param:JsonProperty("deleted") override val deletionDate: Long? = null,
	@param:JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
	@param:JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
	@param:JsonProperty("_conflicts") override val conflicts: List<String>? = null,
	val type: ObjectStorageTaskType,
	val entityClassName: String,
	val entityId: String,
	val attachmentId: String,
	val userId: String,
	val requestTime: Long = System.currentTimeMillis()
) : StoredDocument {
	companion object {
		fun <T : HasDataAttachments<T>> of(entity: T, attachmentId: String, type: ObjectStorageTaskType, userId: String) = ObjectStorageTask(
			UUID.randomUUID().toString(),
			type = type,
			entityClassName = entity::class.java.simpleName.also {
				require(it.isNotBlank()) { "Entity with attachments must have a unique class name." }
			},
			entityId = entity.id,
			attachmentId = attachmentId,
			userId = userId
		)
	}

	override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
	override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

enum class ObjectStorageTaskType { UPLOAD, DELETE }
