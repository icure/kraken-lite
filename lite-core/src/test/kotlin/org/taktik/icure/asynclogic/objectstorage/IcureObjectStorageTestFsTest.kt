package org.taktik.icure.asynclogic.objectstorage

import java.util.UUID
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withTimeout
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageTasksDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.asynclogic.objectstorage.FakeObjectStorageClient.ObjectStoreEvent
import org.taktik.icure.asynclogic.objectstorage.testutils.FakeObjectStorageTasksDAO
import org.taktik.icure.asynclogic.objectstorage.testutils.attachment1
import org.taktik.icure.asynclogic.objectstorage.testutils.attachment2
import org.taktik.icure.asynclogic.objectstorage.testutils.bytes1
import org.taktik.icure.asynclogic.objectstorage.testutils.bytes2
import org.taktik.icure.asynclogic.objectstorage.testutils.document1
import org.taktik.icure.asynclogic.objectstorage.testutils.resetTestLocalStorageDirectory
import org.taktik.icure.asynclogic.objectstorage.testutils.sampleAttachments
import org.taktik.icure.asynclogic.objectstorage.testutils.testObjectStorageProperties
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.objectstorage.ObjectStorageTask
import org.taktik.icure.entities.objectstorage.ObjectStorageTaskType
import org.taktik.icure.properties.ExternalServicesProperties
import org.taktik.icure.test.newId
import org.taktik.icure.test.setCurrentUserData
import org.taktik.icure.test.shouldContainExactly
import org.taktik.icure.utils.toByteArray

private const val STORAGE_TASK_TIMEOUT = 300L



@ExperimentalCoroutinesApi
class IcureObjectStorageTestRamTest : StringSpec({
	testObjectSTorageWith(ExternalServicesProperties(true, true))
})

@ExperimentalCoroutinesApi
class IcureObjectStorageTestFsTest : StringSpec({
	testObjectSTorageWith(ExternalServicesProperties(true, false))
})

fun StringSpec.testObjectSTorageWith(externalServicesProperties: ExternalServicesProperties) {
	val objectStorageProperties = testObjectStorageProperties()
	val localStorage = DocumentLocalObjectStorageImpl(objectStorageProperties)
	val sessionLogic = mockk<SessionInformationProvider>()
	lateinit var eventsChannel: Channel<ObjectStoreEvent>
	lateinit var storageTasksDAO: ObjectStorageTasksDAO
	lateinit var objectStorageClient: FakeObjectStorageClient<Document>
	lateinit var icureObjectStorage: DocumentObjectStorageImpl
	lateinit var userId: String

	beforeEach {
		userId = newId()
		sessionLogic.setCurrentUserData(userId)
		resetTestLocalStorageDirectory()
		eventsChannel = Channel(Channel.UNLIMITED)
		objectStorageClient = FakeObjectStorageClient(
			"documents",
			externalServicesProperties,
			eventsChannel
		) { it == userId }
		storageTasksDAO = FakeObjectStorageTasksDAO()
		icureObjectStorage = DocumentObjectStorageImpl(
			storageTasksDAO,
			object : DocumentObjectStorageClient, ObjectStorageClient<Document> by objectStorageClient {},
			localStorage,
			sessionLogic
		).also { it.afterPropertiesSet() }
	}

	afterEach {
		icureObjectStorage.destroy()
	}

	suspend fun store(document: Document, attachmentId: String, bytes: ByteArray) {
		icureObjectStorage.preStore(document, attachmentId, flowOf(DefaultDataBufferFactory.sharedInstance.wrap(bytes)), bytes.size.toLong())
		icureObjectStorage.scheduleStoreAttachment(document, attachmentId)
	}

	suspend fun storeAndWait(document: Document, attachmentId: String, bytes: ByteArray) {
		store(document, attachmentId, bytes)
		eventsChannel.receive() shouldBe ObjectStoreEvent(document.id, attachmentId, ObjectStoreEvent.Type.SUCCESSFUL_UPLOAD)
	}

	"Object storage should be able to store, read, and delete attachments in the service" {
		sampleAttachments.forEach { icureObjectStorage.preStore(it.first, it.second, flowOf(DefaultDataBufferFactory.sharedInstance.wrap(it.third)), it.third.size.toLong()) }
		shouldThrow<TimeoutCancellationException> { withTimeout(20) { eventsChannel.receive() } }
		sampleAttachments.forEach { icureObjectStorage.scheduleStoreAttachment(it.first, it.second) }
		withTimeout(STORAGE_TASK_TIMEOUT * sampleAttachments.size) {
			sampleAttachments.map { eventsChannel.receive() } shouldContainExactlyInAnyOrder sampleAttachments.map {
				ObjectStoreEvent(it.first.id, it.second, ObjectStoreEvent.Type.SUCCESSFUL_UPLOAD)
			}
		}
		resetTestLocalStorageDirectory() // clear cache -> ensure we are reading from storage client
		sampleAttachments.forEach { icureObjectStorage.readAttachment(it.first, it.second).toByteArray(true) shouldContainExactly it.third }
		val deleted = sampleAttachments[1]
		val remaining = sampleAttachments.filter { it !== deleted }
		icureObjectStorage.scheduleDeleteAttachment(deleted.first, deleted.second)
		withTimeout(STORAGE_TASK_TIMEOUT) {
			eventsChannel.receive() shouldBe ObjectStoreEvent(deleted.first.id, deleted.second, ObjectStoreEvent.Type.SUCCESSFUL_DELETE)
		}
		objectStorageClient.attachmentsKeys shouldContainExactlyInAnyOrder remaining.map { it.first.id to it.second }
		storageTasksDAO.getEntities().toList().shouldBeEmpty()
	}

	"Reading an attachment should also store it in the cache" {
		storeAndWait(document1, attachment1, bytes1)
		resetTestLocalStorageDirectory()
		objectStorageClient.available = false
		shouldThrow<ObjectStorageException> { icureObjectStorage.readAttachment(document1, attachment1).toByteArray(true) }
		objectStorageClient.available = true
		icureObjectStorage.readAttachment(document1, attachment1).toByteArray(true) shouldContainExactly bytes1
		objectStorageClient.available = false
		localStorage.store(document1, attachment1, bytes2) // Ensures the storage job completes, but if then we read bytes2 we know that the job was actually started by this -> error
		icureObjectStorage.readAttachment(document1, attachment1).toByteArray(true) shouldContainExactly bytes1
	}

	"Attempting to update an attachment while service is unavailable should store the task for later execution" {
		objectStorageClient.available = false
		store(document1, attachment1, bytes1)
		icureObjectStorage.scheduleDeleteAttachment(document1, attachment2)
		val expectedTasks = listOf(
			StorageTaskInfo(document1.id, attachment1, ObjectStorageTaskType.UPLOAD),
			StorageTaskInfo(document1.id, attachment2, ObjectStorageTaskType.DELETE)
		)
		// Tasks execution will fail and they will still be stored
		withTimeout(STORAGE_TASK_TIMEOUT * 2) {
			listOf(eventsChannel.receive(), eventsChannel.receive()) shouldContainExactlyInAnyOrder listOf(
				ObjectStoreEvent(document1.id, attachment1, ObjectStoreEvent.Type.UNSUCCESSFUL_UPLOAD),
				ObjectStoreEvent(document1.id, attachment2, ObjectStoreEvent.Type.UNSUCCESSFUL_DELETE)
			)
		}
		storageTasksDAO.getTasksInfo() shouldContainExactlyInAnyOrder expectedTasks
		objectStorageClient.available = true
		// Tasks won't be re-executed automatically
		delay(STORAGE_TASK_TIMEOUT)
		storageTasksDAO.getTasksInfo() shouldContainExactlyInAnyOrder expectedTasks
		icureObjectStorage.hasScheduledStorageTasks shouldBe false
		// Now tasks should complete successfully
		icureObjectStorage.rescheduleFailedStorageTasks()
		withTimeout(STORAGE_TASK_TIMEOUT * 2) {
			listOf(eventsChannel.receive(), eventsChannel.receive()) shouldContainExactlyInAnyOrder listOf(
				ObjectStoreEvent(document1.id, attachment1, ObjectStoreEvent.Type.SUCCESSFUL_UPLOAD),
				ObjectStoreEvent(document1.id, attachment2, ObjectStoreEvent.Type.SUCCESSFUL_DELETE)
			)
		}
	}

	"If there are multiple tasks for the same attachment only the latest should be executed" {
		val tasks = (0 until 100).map {
			ObjectStorageTask(
				UUID.randomUUID().toString(),
				entityClassName = Document::class.java.simpleName,
				entityId = document1.id,
				attachmentId = attachment1,
				type = if (it % 2 == 0) ObjectStorageTaskType.UPLOAD else ObjectStorageTaskType.DELETE,
				requestTime = 100L + (it * 100),
				userId = userId
			)
		}
		icureObjectStorage.preStore(document1, attachment1, flowOf(DefaultDataBufferFactory.sharedInstance.wrap(bytes1)), bytes1.size.toLong())
		tasks.shuffled().forEach { storageTasksDAO.save(it) }
		icureObjectStorage.rescheduleFailedStorageTasks()
		while (icureObjectStorage.hasScheduledStorageTasks) {
			println("Not done yet")
			delay(100)
		}
		// Task has completed
		var clientCallCount = 0
		while (eventsChannel.tryReceive().getOrNull() != null) clientCallCount += 1
		clientCallCount shouldBe 1
		// Latest task is upload
		icureObjectStorage.readAttachment(document1, attachment1).toByteArray(true) shouldContainExactly bytes1
	}
}

private suspend fun ObjectStorageTasksDAO.getTasksInfo() = getEntities().toList().map {
	StorageTaskInfo(it.entityId, it.attachmentId, it.type)
}

private data class StorageTaskInfo(val documentId: String, val attachmentId: String, val type: ObjectStorageTaskType)
