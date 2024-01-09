package org.taktik.icure.asynclogic.objectstorage

import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Files
import java.util.UUID
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.Document
import org.taktik.icure.properties.ExternalServicesProperties
import org.taktik.icure.utils.toByteArray

/**
 * Fake implementation of [ObjectStorageClient], for testing purposes only (including creation of a test container).
 * @param entityGroupName group name of entities supported by this object storage client (e.g. documents)
 * @param externalServicesProperties properties of external services for the application.
 * @param eventsChannel if not null methods invoked on this implementation will be logged to the event channel.
 * @param checkUser verifies if a user can actually use this client, for testing purposes.
 */
class FakeObjectStorageClient<T : HasDataAttachments<T>>(
	override val entityGroupName: String,
	externalServicesProperties: ExternalServicesProperties,
	private val eventsChannel: Channel<ObjectStoreEvent>?,
	private val checkUser: (String) -> Boolean
) : ObjectStorageClient<T> {
	companion object {
		/**
		 * Creates a fake [DocumentObjectStorageClient], using a [FakeObjectStorageClient] as base for the implementation.
		 * @param externalServicesProperties see [FakeObjectStorageClient]'s constructor
		 * @param eventsChannel see [FakeObjectStorageClient]'s constructor
		 * @param checkUser see [FakeObjectStorageClient]'s constructor
		 */
		fun document(
			externalServicesProperties: ExternalServicesProperties,
			eventsChannel: Channel<ObjectStoreEvent>?,
			checkUser: (String) -> Boolean
		): DocumentObjectStorageClient =
			object : DocumentObjectStorageClient, ObjectStorageClient<Document> by FakeObjectStorageClient(
				"documents",
				externalServicesProperties,
				eventsChannel,
				checkUser
			) {}
	}

	var available = true

	private val storageDirectory = if (!externalServicesProperties.storeFakeObjectStorageInRam) {
		Files.createTempDirectory("icure-fakeobjectstorage-").toFile().also { it.deleteOnExit() }
	} else null
	// Currently there is no need for thread safety, since the object storage implementation should ensure only one store/delete
	// operation is executed at a time
	private val entityToAttachments = mutableMapOf<String, MutableMap<String, StoredData>>()

	val attachmentsKeys get() = entityToAttachments.flatMap { (docId, attachments) -> attachments.keys.map { docId to it } }

	override suspend fun upload(entity: T, attachmentId: String, content: Flow<DataBuffer>, userId: String): Boolean =
		unsafeUpload(entity.id, attachmentId, content, userId)

	override suspend fun unsafeUpload(entityId: String, attachmentId: String, content: Flow<DataBuffer>, userId: String): Boolean {
		checkUser(userId)
		return if (available) {
			val entityAttachments = entityToAttachments.computeIfAbsent(entityId) { mutableMapOf() }
			if (!entityAttachments.containsKey(attachmentId)) {
				val storedObject = storageDirectory?.let { dir ->
					val objSubdir = Random.nextInt(255).toString()
					val objUUID = UUID.randomUUID().toString()
					val objFile = dir.resolve(objSubdir).also { it.mkdir() }.resolve(objUUID)
					withContext(Dispatchers.IO) {
						RandomAccessFile(objFile, "rw").channel.use { channel ->
							content.collect {
								it.asByteBuffer().let { byteBuffer ->
									while (byteBuffer.hasRemaining()) {
										channel.write(byteBuffer)
									}
								}
							}
						}
					}
					StoredData.Filesystem(objFile)
				} ?: StoredData.Ram(content.toByteArray(true))
				entityAttachments[attachmentId] = storedObject
			}
			eventsChannel?.send(ObjectStoreEvent(entityId, attachmentId, ObjectStoreEvent.Type.SUCCESSFUL_UPLOAD))
			true
		} else {
			eventsChannel?.send(ObjectStoreEvent(entityId, attachmentId, ObjectStoreEvent.Type.UNSUCCESSFUL_UPLOAD))
			false
		}
	}

	override fun get(entity: T, attachmentId: String, userId: String): Flow<DataBuffer> {
		checkUser(userId)
		return if (available) {
			entityToAttachments[entity.id]?.get(attachmentId)?.asFlow()
				?: throw IllegalStateException("Document does not exist. Available attachments: $attachmentsKeys")
		} else throw ObjectStorageException("Storage service is unavailable", null)
	}

	override suspend fun checkAvailable(entity: T, attachmentId: String, userId: String): Boolean {
		checkUser(userId)
		return entityToAttachments[entity.id]?.let { attachmentId in it } == true
	}

	override suspend fun delete(entity: T, attachmentId: String, userId: String): Boolean {
		checkUser(userId)
		return unsafeDelete(entity.id, attachmentId, userId)
	}

	override suspend fun unsafeDelete(entityId: String, attachmentId: String, userId: String): Boolean {
		checkUser(userId)
		return if (available) {
			entityToAttachments[entityId]?.remove(attachmentId)
			eventsChannel?.send(ObjectStoreEvent(entityId, attachmentId, ObjectStoreEvent.Type.SUCCESSFUL_DELETE))
			true
		} else {
			eventsChannel?.send(ObjectStoreEvent(entityId, attachmentId, ObjectStoreEvent.Type.UNSUCCESSFUL_DELETE))
			false
		}
	}

	data class ObjectStoreEvent(val documentId: String, val attachmentId: String, val type: Type) {
		enum class Type { SUCCESSFUL_UPLOAD, SUCCESSFUL_DELETE, UNSUCCESSFUL_UPLOAD, UNSUCCESSFUL_DELETE }
	}

	sealed class StoredData {
		abstract fun asFlow(): Flow<DataBuffer>

		class Ram(val data: ByteArray): StoredData() {
			override fun asFlow(): Flow<DataBuffer> =
				flowOf(DefaultDataBufferFactory.sharedInstance.wrap(data))
		}
		class Filesystem(val file: File): StoredData() {
			override fun asFlow(): Flow<DataBuffer> =
				DataBufferUtils.readAsynchronousFileChannel(
					{ AsynchronousFileChannel.open(file.toPath()) },
					0,
					DefaultDataBufferFactory.sharedInstance,
					10000
				).asFlow()
		}
	}
}
