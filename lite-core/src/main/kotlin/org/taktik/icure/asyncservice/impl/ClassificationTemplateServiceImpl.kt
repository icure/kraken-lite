package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.icure.asynclogic.ClassificationTemplateLogic
import org.taktik.icure.asyncservice.ClassificationTemplateService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.ClassificationTemplate
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.pagination.PaginationElement

@Service
class ClassificationTemplateServiceImpl(
    private val classificationTemplateLogic: ClassificationTemplateLogic
) : ClassificationTemplateService {
    override suspend fun createClassificationTemplate(classificationTemplate: ClassificationTemplate): ClassificationTemplate? = classificationTemplateLogic.createClassificationTemplate(classificationTemplate)

    override suspend fun getClassificationTemplate(classificationTemplateId: String): ClassificationTemplate? = classificationTemplateLogic.getClassificationTemplate(classificationTemplateId)

    override fun deleteClassificationTemplates(ids: Collection<String>): Flow<DocIdentifier> = classificationTemplateLogic.deleteClassificationTemplates(ids.toSet())

    override suspend fun deleteClassificationTemplate(classificationTemplateId: String): DocIdentifier = classificationTemplateLogic.deleteClassificationTemplates(setOf(classificationTemplateId)).single()

    override suspend fun modifyClassificationTemplate(classificationTemplate: ClassificationTemplate): ClassificationTemplate = classificationTemplateLogic.modifyEntities(setOf(classificationTemplate)).single()

    override fun getClassificationTemplates(ids: List<String>): Flow<ClassificationTemplate> = classificationTemplateLogic.getClassificationTemplates(ids)

    override fun listClassificationTemplates(paginationOffset: PaginationOffset<String>): Flow<PaginationElement> = classificationTemplateLogic.listClassificationTemplates(paginationOffset)
}