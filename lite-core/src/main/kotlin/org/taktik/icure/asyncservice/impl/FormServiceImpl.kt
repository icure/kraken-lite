package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.FormLogic
import org.taktik.icure.asyncservice.FormService
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult

@Service
class FormServiceImpl(
    private val formLogic: FormLogic
) : FormService {
    override suspend fun getForm(id: String): Form? = formLogic.getForm(id)

    override fun getForms(selectedIds: Collection<String>): Flow<Form> = formLogic.getForms(selectedIds)

    override fun listFormsByHCPartyAndPatient(
        hcPartyId: String,
        secretPatientKeys: List<String>,
        healthElementId: String?,
        planOfActionId: String?,
        formTemplateId: String?
    ): Flow<Form> = formLogic.listFormsByHCPartyAndPatient(hcPartyId, secretPatientKeys, healthElementId, planOfActionId, formTemplateId)

    override fun listFormIdsByDataOwnerPatientOpeningDate(
        dataOwnerId: String,
        secretForeignKeys: Set<String>,
        startDate: Long?,
        endDate: Long?,
        descending: Boolean
    ): Flow<String> = formLogic.listFormIdsByDataOwnerPatientOpeningDate(dataOwnerId, secretForeignKeys, startDate, endDate, descending)

    override suspend fun addDelegation(formId: String, delegation: Delegation): Form? = formLogic.addDelegation(formId, delegation)

    override suspend fun createForm(form: Form): Form? = formLogic.createForm(form)

    override fun deleteForms(ids: Set<String>): Flow<DocIdentifier> = formLogic.deleteForms(ids)

    override suspend fun deleteForm(id: String): DocIdentifier = deleteForms(setOf(id)).single()

    override suspend fun modifyForm(form: Form): Form? = formLogic.modifyForm(form)

    override fun listByHcPartyAndParentId(hcPartyId: String, formId: String): Flow<Form> = formLogic.listByHcPartyAndParentId(hcPartyId, formId)

    override suspend fun addDelegations(formId: String, delegations: List<Delegation>): Form? = formLogic.addDelegations(formId, delegations)

    override fun solveConflicts(limit: Int?, ids: List<String>?) = formLogic.solveConflicts(limit, ids)

    override suspend fun getAllByLogicalUuid(formUuid: String): List<Form> = formLogic.getAllByLogicalUuid(formUuid)

    override suspend fun getAllByUniqueId(lid: String): List<Form> = formLogic.getAllByUniqueId(lid)

    override fun modifyForms(forms: Collection<Form>): Flow<Form> = formLogic.modifyEntities(forms)

    override fun createForms(forms: Collection<Form>): Flow<Form> = formLogic.createEntities(forms)

    override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<Form>> = formLogic.bulkShareOrUpdateMetadata(requests)
}
