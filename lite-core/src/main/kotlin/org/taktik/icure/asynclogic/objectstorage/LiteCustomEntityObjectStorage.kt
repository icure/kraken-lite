package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import org.taktik.icure.entities.CustomEntityBase

@Service
class LiteCustomEntityObjectStorage : CustomEntityObjectStorage {
	override suspend fun preStore(
		entity: CustomEntityBase,
		attachmentId: String,
		content: Flow<DataBuffer>,
		size: Long
	) {
		throw NotImplementedError()
	}

	override suspend fun scheduleStoreAttachment(
		entity: CustomEntityBase,
		attachmentId: String
	) {
		throw NotImplementedError()
	}

	override fun readAttachment(
		entity: CustomEntityBase,
		attachmentId: String
	): Flow<DataBuffer> {
		throw NotImplementedError()
	}

	override fun tryReadCachedAttachment(
		entity: CustomEntityBase,
		attachmentId: String
	): Flow<DataBuffer>? {
		throw NotImplementedError()
	}

	override suspend fun hasStoredAttachment(
		entity: CustomEntityBase,
		attachmentId: String
	): Boolean {
		throw NotImplementedError()
	}

	override suspend fun scheduleDeleteAttachment(
		entity: CustomEntityBase,
		attachmentId: String
	) {
		throw NotImplementedError()
	}

	override suspend fun rescheduleFailedStorageTasks() {
		// TODO
	}
}