package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.ClassificationTemplateLogic
import org.taktik.icure.asyncservice.ClassificationTemplateService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.ClassificationTemplate
import org.taktik.icure.pagination.PaginationElement

@Service
class ClassificationTemplateServiceImpl(
    private val classificationTemplateLogic: ClassificationTemplateLogic
) : ClassificationTemplateService {
    override suspend fun createClassificationTemplate(classificationTemplate: ClassificationTemplate): ClassificationTemplate? = classificationTemplateLogic.createClassificationTemplate(classificationTemplate)
    override suspend fun getClassificationTemplate(classificationTemplateId: String): ClassificationTemplate? = classificationTemplateLogic.getClassificationTemplate(classificationTemplateId)
    override fun deleteClassificationTemplates(ids: Collection<String>): Flow<DocIdentifier> = classificationTemplateLogic.deleteEntities(ids.map { IdAndRev(it, null) })
    override suspend fun deleteClassificationTemplate(classificationTemplateId: String): DocIdentifier = classificationTemplateLogic.deleteEntity(classificationTemplateId, null)
    override suspend fun modifyClassificationTemplate(classificationTemplate: ClassificationTemplate): ClassificationTemplate = classificationTemplateLogic.modifyEntities(setOf(classificationTemplate)).single()
    override fun getClassificationTemplates(ids: List<String>): Flow<ClassificationTemplate> = classificationTemplateLogic.getClassificationTemplates(ids)
    override fun listClassificationTemplates(paginationOffset: PaginationOffset<String>): Flow<PaginationElement> = classificationTemplateLogic.listClassificationTemplates(paginationOffset)
}