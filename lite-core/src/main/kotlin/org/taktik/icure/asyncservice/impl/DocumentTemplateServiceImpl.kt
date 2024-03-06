package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
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

    override fun getDocumentTemplatesBySpecialty(specialityCode: String): Flow<DocumentTemplate> = documentTemplateLogic.getDocumentTemplatesBySpecialty(specialityCode)

    override fun getDocumentTemplatesByDocumentType(documentTypeCode: String): Flow<DocumentTemplate> = documentTemplateLogic.getDocumentTemplatesByDocumentType(documentTypeCode)

    override fun getDocumentTemplatesByDocumentTypeAndUser(
        documentTypeCode: String,
        userId: String
    ): Flow<DocumentTemplate> = documentTemplateLogic.getDocumentTemplatesByDocumentTypeAndUser(documentTypeCode, userId)

    override fun getDocumentTemplatesByUser(userId: String): Flow<DocumentTemplate> = documentTemplateLogic.getDocumentTemplatesByUser(userId)

    override suspend fun modifyDocumentTemplate(documentTemplate: DocumentTemplate): DocumentTemplate? = documentTemplateLogic.modifyDocumentTemplate(documentTemplate)

    override fun createDocumentTemplates(entities: Collection<DocumentTemplate>): Flow<DocumentTemplate> = documentTemplateLogic.createEntities(entities)

    override fun deleteDocumentTemplates(ids: Set<String>): Flow<DocIdentifier> = documentTemplateLogic.deleteEntities(ids)
    override fun getAllDocumentTemplates(paginationOffset: PaginationOffset<String>): Flow<PaginationElement> = documentTemplateLogic.getAllDocumentTemplates(paginationOffset)
}