package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.FormTemplateLogic
import org.taktik.icure.asyncservice.FormTemplateService
import org.taktik.icure.entities.FormTemplate

@Service
class FormTemplateServiceImpl(
    private val formTemplateLogic: FormTemplateLogic
) : FormTemplateService {
    override fun createFormTemplates(
        entities: Collection<FormTemplate>,
        createdEntities: Collection<FormTemplate>
    ): Flow<FormTemplate> = formTemplateLogic.createFormTemplates(entities, createdEntities)

    override suspend fun createFormTemplate(entity: FormTemplate): FormTemplate = formTemplateLogic.createFormTemplate(entity)

    override suspend fun getFormTemplate(formTemplateId: String): FormTemplate? = formTemplateLogic.getFormTemplate(formTemplateId)

    override fun getFormTemplatesByGuid(
        userId: String,
        specialityCode: String,
        formTemplateGuid: String
    ): Flow<FormTemplate> = formTemplateLogic.getFormTemplatesByGuid(userId, specialityCode, formTemplateGuid)

    override fun getFormTemplatesBySpecialty(specialityCode: String, loadLayout: Boolean): Flow<FormTemplate> = formTemplateLogic.getFormTemplatesBySpecialty(specialityCode, loadLayout)

    override fun getFormTemplatesByUser(userId: String, loadLayout: Boolean): Flow<FormTemplate> = formTemplateLogic.getFormTemplatesByUser(userId, loadLayout)

    override suspend fun modifyFormTemplate(formTemplate: FormTemplate): FormTemplate? = formTemplateLogic.modifyFormTemplate(formTemplate)

    override fun deleteFormTemplates(ids: Set<String>): Flow<DocIdentifier> = formTemplateLogic.deleteEntities(ids.map { IdAndRev(it, null) })
}