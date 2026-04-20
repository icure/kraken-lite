package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.FormTemplateLogic
import org.taktik.icure.asyncservice.FormTemplateService
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.FormTemplate

@Service
class FormTemplateServiceImpl(
	private val formTemplateLogic: FormTemplateLogic
) : FormTemplateService {
	override fun createFormTemplates(
		entities: Collection<FormTemplate>,
		createdEntities: Collection<FormTemplate>
	): Flow<FormTemplate> = formTemplateLogic.createFormTemplates(entities, createdEntities)

	override fun modifyFormTemplates(formTemplates: List<FormTemplate>): Flow<FormTemplate> = formTemplateLogic.modifyEntities(formTemplates)
	override fun getFormTemplates(formTemplateIds: List<String>): Flow<FormTemplate> = formTemplateLogic.getEntities(formTemplateIds)
	override suspend fun createFormTemplate(entity: FormTemplate): FormTemplate = formTemplateLogic.createEntity(entity)
	override suspend fun getFormTemplate(formTemplateId: String): FormTemplate? = formTemplateLogic.getEntity(formTemplateId)

	@Suppress("DEPRECATION")
	@Deprecated("This method has unintuitive behaviour, read FormTemplateService.getFormTemplatesByGuid doc for more info")
	override fun getFormTemplatesByGuid(
		userId: String,
		specialityCode: String,
		formTemplateGuid: String
	): Flow<FormTemplate> = formTemplateLogic.getFormTemplatesByGuid(userId, specialityCode, formTemplateGuid)

	@Suppress("DEPRECATION")
	@Deprecated("Use matchFormTemplatesBy with a FormTemplateBySpecialtyFilter instead")
	override fun getFormTemplatesBySpecialty(specialityCode: String, loadLayout: Boolean): Flow<FormTemplate> =
		formTemplateLogic.getFormTemplatesBySpecialty(specialityCode, loadLayout)
	override fun getFormTemplatesByUser(userId: String, loadLayout: Boolean): Flow<FormTemplate> = formTemplateLogic.getFormTemplatesByUser(userId, loadLayout)
	override fun matchFormTemplatesBy(filter: AbstractFilter<FormTemplate>): Flow<String> =
		formTemplateLogic.matchEntitiesBy(filter)

	override suspend fun undeleteFormTemplate(
		formTemplateId: String,
		rev: String
	): FormTemplate = formTemplateLogic.deleteEntity(formTemplateId, rev)

	override suspend fun deleteFormTemplate(id: String, rev: String?): DocIdentifier = formTemplateLogic.deleteEntity(id, rev).let { DocIdentifier(it.id, it.rev) }

	override suspend fun modifyFormTemplate(formTemplate: FormTemplate): FormTemplate = formTemplateLogic.modifyEntity(formTemplate)

	override fun deleteFormTemplates(ids: List<String>): Flow<FormTemplate> = formTemplateLogic.deleteEntities(ids.map { IdAndRev(it, null) })
	override fun deleteFormTemplatesWithRev(formTemplateIds: List<IdAndRev>): Flow<DocIdentifier> = formTemplateLogic.deleteEntities(formTemplateIds).map { DocIdentifier(it.id, it.rev) }
	override fun undeleteFormTemplates(formTemplateIds: List<IdAndRev>): Flow<FormTemplate> = formTemplateLogic.undeleteEntities(formTemplateIds)
	override suspend fun purgeFormTemplate(id: String, rev: String): DocIdentifier = formTemplateLogic.purgeEntity(id, rev)
	override fun purgeFormTemplates(formTemplateIds: List<IdAndRev>): Flow<DocIdentifier> = formTemplateLogic.purgeEntities(formTemplateIds)
}