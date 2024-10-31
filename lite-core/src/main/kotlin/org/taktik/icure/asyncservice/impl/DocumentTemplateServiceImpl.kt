package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.DocumentTemplateLogic
import org.taktik.icure.asyncservice.DocumentTemplateService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.DocumentTemplate
import org.taktik.icure.pagination.PaginationElement

@Service
class DocumentTemplateServiceImpl(
    private val documentTemplateLogic: DocumentTemplateLogic
) : DocumentTemplateService {
    override suspend fun createDocumentTemplate(documentTemplate: DocumentTemplate): DocumentTemplate = documentTemplateLogic.createDocumentTemplate(documentTemplate)

    override suspend fun getDocumentTemplate(documentTemplateId: String): DocumentTemplate? = documentTemplateLogic.getDocumentTemplate(documentTemplateId)

    override fun getDocumentTemplatesBySpecialty(specialityCode: String, loadAttachment: Boolean): Flow<DocumentTemplate> =
        documentTemplateLogic.getDocumentTemplatesBySpecialty(specialityCode, loadAttachment)

    override fun getDocumentTemplatesByDocumentType(documentTypeCode: String, loadAttachment: Boolean): Flow<DocumentTemplate> =
        documentTemplateLogic.getDocumentTemplatesByDocumentType(documentTypeCode, loadAttachment)

    override fun getDocumentTemplatesByDocumentTypeAndUser(
        documentTypeCode: String,
        userId: String,
        loadAttachment: Boolean
    ): Flow<DocumentTemplate> = documentTemplateLogic.getDocumentTemplatesByDocumentTypeAndUser(documentTypeCode, userId, loadAttachment)

    override fun getDocumentTemplatesByUser(userId: String, loadAttachment: Boolean): Flow<DocumentTemplate> =
        documentTemplateLogic.getDocumentTemplatesByUser(userId, loadAttachment)

    override suspend fun modifyDocumentTemplate(documentTemplate: DocumentTemplate): DocumentTemplate? = documentTemplateLogic.modifyDocumentTemplate(documentTemplate)
    override fun deleteDocumentTemplates(ids: List<IdAndRev>): Flow<DocIdentifier> = documentTemplateLogic.deleteEntities(ids)
    override fun getAllDocumentTemplates(paginationOffset: PaginationOffset<String>): Flow<PaginationElement> = documentTemplateLogic.getAllDocumentTemplates(paginationOffset)
    override fun getAllDocumentTemplates(): Flow<DocumentTemplate> = documentTemplateLogic.getEntities()
}