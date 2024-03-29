package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.base.HasDataAttachments

/**
 * Handles interaction with the object storage service.
 */
interface ObjectStorageClient<T : HasDataAttachments<T>> {
	/**
	 * Unique name of the type/group of entities for this client.
	 */
	val entityGroupName: String

	/**
	 * Stores an attachment in the attachment storage service.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment.
	 * @param content the attachment content
	 * @param userId id of the user which performed the request.
	 * @return if storing was successful: either there was already a matching attachment for the document or it was successfully added.
	 */
	suspend fun upload(entity: T, attachmentId: String, content: Flow<DataBuffer>, userId: String): Boolean

	/**
	 * Stores an attachment in the attachment storage service. Unsafe because instead of taking a full entity it just takes the id, which
	 * is more prone to programming error, but is useful in cases where we only know the id.
	 * @param entityId id of the entity which owns the attachment.
	 * @param attachmentId id of the attachment.
	 * @param content the attachment content
	 * @param userId id of the user which performed the request.
	 * @return if storing was successful: either there was already a matching attachment for the document or it was successfully added.
	 */
	suspend fun unsafeUpload(entityId: String, attachmentId: String, content: Flow<DataBuffer>, userId: String): Boolean

	/**
	 * Reads the attachment. Throws exceptions in case the storage service is not reachable or the attachment does not exist.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment.
	 * @param userId id of the user which performed the request.
	 * @return the attachment content.
	 */
	fun get(entity: T, attachmentId: String, userId: String): Flow<DataBuffer>

	/**
	 * Checks if a specific attachment is available.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment.
	 * @param userId id of the user which performed the request.
	 * @return true if the storage service is available and reachable.
	 */
	suspend fun checkAvailable(entity: T, attachmentId: String, userId: String): Boolean

	/**
	 * Deletes an attachment from the storage service.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment.
	 * @param userId id of the user which performed the request.
	 * @return if deletion was successful: either there was no matching attachment for the document or it was successfully deleted.
	 */
	suspend fun delete(entity: T, attachmentId: String, userId: String): Boolean

	/**
	 * Deletes an attachment from the storage service. Unsafe because instead of taking a full entity it just takes the id, which
	 * is more prone to programming error, but is useful in cases where we only know the id.
	 * @param entityId id of the entity which owns the attachment.
	 * @param attachmentId id of the attachment.
	 * @param userId id of the user which performed the request.
	 * @return if deletion was successful: either there was no matching attachment for the document or it was successfully deleted.
	 */
	suspend fun unsafeDelete(entityId: String, attachmentId: String, userId: String): Boolean
}

interface DocumentObjectStorageClient : ObjectStorageClient<Document>
