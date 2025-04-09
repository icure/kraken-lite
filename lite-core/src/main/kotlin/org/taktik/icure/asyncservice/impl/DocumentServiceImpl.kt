package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.objectstorage.DataAttachmentChange
import org.taktik.icure.asyncservice.DocumentService
import org.taktik.icure.domain.BatchUpdateDocumentInfo
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult
import java.nio.ByteBuffer

@Service
class DocumentServiceImpl(
    private val documentLogic: DocumentLogic
) : DocumentService {
    override suspend fun createDocument(document: Document, strict: Boolean): Document? = documentLogic.createDocument(document, strict)

    override suspend fun getMainAttachment(documentId: String): Flow<DataBuffer> = documentLogic.getMainAttachment(documentId)

    override suspend fun getDocument(documentId: String): Document? = documentLogic.getDocument(documentId)

    override fun getAttachment(documentId: String, attachmentId: String): Flow<ByteBuffer> = documentLogic.getAttachment(documentId, attachmentId)

    override suspend fun modifyDocument(
        updatedDocument: Document,
        currentDocument: Document,
        strict: Boolean
    ): Document = requireNotNull(
        documentLogic.modifyDocument(updatedDocument, strict)
    ) { "Logic returned null on Document update" }

    override fun createOrModifyDocuments(documents: List<BatchUpdateDocumentInfo>, strict: Boolean): Flow<Document> = documentLogic.createOrModifyDocuments(documents, strict)

    override suspend fun updateAttachments(
        currentDocument: Document,
        mainAttachmentChange: DataAttachmentChange?,
        secondaryAttachmentsChanges: Map<String, DataAttachmentChange>
    ): Document? = documentLogic.updateAttachments(currentDocument, mainAttachmentChange, secondaryAttachmentsChanges)

    override fun listDocumentsByDocumentTypeHCPartySecretMessageKeys(
        documentTypeCode: String,
        hcPartyId: String,
        secretForeignKeys: List<String>
    ): Flow<Document> = documentLogic.listDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode, hcPartyId, secretForeignKeys)

    @Suppress("DEPRECATION")
    @Deprecated("This method cannot include results with secure delegations, use listDocumentIdsByDataOwnerPatientCreated instead")
    override fun listDocumentsByHCPartySecretMessageKeys(
        hcPartyId: String,
        secretForeignKeys: List<String>
    ): Flow<Document> = documentLogic.listDocumentsByHCPartySecretMessageKeys(hcPartyId, secretForeignKeys)

    override fun listDocumentIdsByDataOwnerPatientCreated(
        dataOwnerId: String,
        secretForeignKeys: Set<String>,
        startDate: Long?,
        endDate: Long?,
        descending: Boolean
    ): Flow<String> = documentLogic.listDocumentIdsByDataOwnerPatientCreated(dataOwnerId, secretForeignKeys, startDate, endDate, descending)

    override fun listDocumentsWithoutDelegation(limit: Int): Flow<Document> = documentLogic.listDocumentsWithoutDelegation(limit)

    override fun getDocuments(documentIds: List<String>): Flow<Document> = documentLogic.getDocuments(documentIds)

    override fun solveConflicts(limit: Int?, ids: List<String>?): Flow<IdAndRev> = documentLogic.solveConflicts(limit, ids)

    override suspend fun getDocumentsByExternalUuid(documentId: String): List<Document> = documentLogic.getDocumentsByExternalUuid(documentId)
    override fun deleteDocuments(ids: List<IdAndRev>): Flow<Document> = documentLogic.deleteEntities(ids)

    override suspend fun deleteDocument(id: String, rev: String?): Document = documentLogic.deleteEntity(id, rev)

    override suspend fun purgeDocument(id: String, rev: String): DocIdentifier = documentLogic.purgeEntity(id, rev)

    override suspend fun undeleteDocument(id: String, rev: String): Document = documentLogic.undeleteEntity(id, rev)

    override suspend fun modifyDocuments(documents: Collection<Document>): Flow<Document> = documentLogic.modifyEntities(documents)
    override fun matchDocumentsBy(filter: AbstractFilter<Document>): Flow<String> = documentLogic.matchEntitiesBy(filter)

    override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<Document>> = documentLogic.bulkShareOrUpdateMetadata(requests)
}