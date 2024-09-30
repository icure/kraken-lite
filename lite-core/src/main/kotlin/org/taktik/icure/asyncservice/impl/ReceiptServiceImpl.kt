package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.ReceiptLogic
import org.taktik.icure.asyncservice.ReceiptService
import org.taktik.icure.entities.Receipt
import org.taktik.icure.entities.embed.ReceiptBlobType
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult
import java.nio.ByteBuffer

@Service
class ReceiptServiceImpl(
    private val receiptLogic: ReceiptLogic
) : ReceiptService {
    override suspend fun createReceipt(receipt: Receipt): Receipt? = receiptLogic.createReceipt(receipt)

    override suspend fun modifyReceipt(receipt: Receipt): Receipt = receiptLogic.modifyEntities(listOf(receipt)).single()

    override fun listReceiptsByReference(ref: String): Flow<Receipt> = receiptLogic.listReceiptsByReference(ref)

    override fun getAttachment(receiptId: String, attachmentId: String): Flow<ByteBuffer> = receiptLogic.getAttachment(receiptId, attachmentId)

    override suspend fun addReceiptAttachment(
        receipt: Receipt,
        blobType: ReceiptBlobType,
        payload: ByteArray
    ): Receipt = receiptLogic.addReceiptAttachment(receipt, blobType, payload)

    override fun createReceipts(receipts: Collection<Receipt>): Flow<Receipt> = receiptLogic.createEntities(receipts)
    override fun deleteReceipts(ids: List<IdAndRev>): Flow<DocIdentifier> = receiptLogic.deleteEntities(ids)
    override suspend fun deleteReceipt(id: String, rev: String?): DocIdentifier = receiptLogic.deleteEntity(id, rev)
    override suspend fun purgeReceipt(id: String, rev: String): DocIdentifier = receiptLogic.purgeEntity(id, rev)
    override suspend fun undeleteReceipt(id: String, rev: String): Receipt = receiptLogic.undeleteEntity(id, rev)
    override suspend fun getReceipt(id: String): Receipt? = receiptLogic.getEntity(id)

    override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<Receipt>> = receiptLogic.bulkShareOrUpdateMetadata(requests)
}
