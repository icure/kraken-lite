package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.ReceiptLogic
import org.taktik.icure.asyncservice.ReceiptService
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.entities.Receipt
import org.taktik.icure.entities.conflicts.ConflictResolutionResult
import org.taktik.icure.entities.conflicts.MergeResult
import org.taktik.icure.entities.embed.ReceiptBlobType
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult
import java.nio.ByteBuffer

@Service
class ReceiptServiceImpl(
	private val receiptLogic: ReceiptLogic
) : ReceiptService {
	override suspend fun createReceipt(receipt: Receipt): Receipt = receiptLogic.createReceipt(receipt)

	override suspend fun modifyReceipt(receipt: Receipt): Receipt = receiptLogic.modifyEntity(receipt)
	override fun modifyReceipts(receipts: List<Receipt>): Flow<Receipt> = receiptLogic.modifyEntities(receipts)

	override fun listReceiptsByReference(ref: String): Flow<Receipt> = receiptLogic.listReceiptsByReference(ref)

	override fun getAttachment(receiptId: String, attachmentId: String): Flow<ByteBuffer> = receiptLogic.getAttachment(receiptId, attachmentId)

	override suspend fun addReceiptAttachment(
		receipt: Receipt,
		blobType: ReceiptBlobType,
		payload: ByteArray
	): Receipt = receiptLogic.addReceiptAttachment(receipt, blobType, payload)

	override fun createReceipts(receipts: Collection<Receipt>): Flow<Receipt> = receiptLogic.createEntities(receipts)
	override fun deleteReceipts(ids: List<IdAndRev>): Flow<Receipt> = receiptLogic.deleteEntities(ids)
	override suspend fun deleteReceipt(id: String, rev: String?): Receipt = receiptLogic.deleteEntity(id, rev)
	override suspend fun purgeReceipt(id: String, rev: String): DocIdentifier = receiptLogic.purgeEntity(id, rev)
	override fun purgeReceipts(receiptIds: List<IdAndRev>): Flow<DocIdentifier> = receiptLogic.purgeEntities(receiptIds)

	override suspend fun undeleteReceipt(id: String, rev: String): Receipt = receiptLogic.undeleteEntity(id, rev)
	override fun undeleteReceipts(receiptIds: List<IdAndRev>): Flow<Receipt> = receiptLogic.undeleteEntities(receiptIds)

	override suspend fun getReceipt(id: String): Receipt? = receiptLogic.getEntity(id)
	override fun getReceipts(receiptIds: List<String>): Flow<Receipt> = receiptLogic.getEntities(receiptIds)

	override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<Receipt>> = receiptLogic.bulkShareOrUpdateMetadata(requests)

	override fun getConflictingEntitiesIds(): Flow<String> = receiptLogic.getConflictingEntitiesIds()
	override fun getConflictsFor(entityId: String): Flow<Receipt> = receiptLogic.getConflictsFor(entityId)
	override suspend fun declareConflictWinner(
		entity: Receipt,
		conflictsToPurge: List<String>
	): ConflictResolutionResult<Receipt> {
		val conflicts = conflictsToPurge.mapNotNull { rev ->
			receiptLogic.getBypassingCache(entity.id, rev)
		}
		return receiptLogic.declareConflictWinner(entity, conflicts)
	}
	override fun solveConflicts(
		limit: Int?,
		ids: List<String>?
	): Flow<MergeResult> = receiptLogic.solveConflicts(limit, ids)

	override suspend fun putReceiptAttachmentInfo(
		receiptId: String,
		receiptRev: String,
		blobType: ReceiptBlobType,
		compressionAlgorithm: String?,
		triedCompressionAlgorithmsVersion: String?,
		realDataSize: Long?,
		storedDataSize: Long,
		data: Flow<DataBuffer>
	): Receipt =
		receiptLogic.putReceiptAttachmentInfo(
			receiptId,
			receiptRev,
			blobType,
			compressionAlgorithm,
			triedCompressionAlgorithmsVersion,
			realDataSize,
			storedDataSize,
			data
		)

	override fun getDataAttachmentByBlobType(
		receiptId: String,
		blobType: ReceiptBlobType
	): Flow<DataBuffer> =
		receiptLogic.getDataAttachmentByBlobType(receiptId, blobType)
}
