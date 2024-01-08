/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.base.EntityWithSecureDelegationsLogic
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.embed.Delegation
import org.taktik.couchdb.entity.IdAndRev

interface FormLogic : EntityPersister<Form, String>, EntityWithSecureDelegationsLogic<Form> {
	suspend fun getForm(id: String): Form?
	fun getForms(selectedIds: Collection<String>): Flow<Form>
	fun listFormsByHCPartyAndPatient(hcPartyId: String, secretPatientKeys: List<String>, healthElementId: String?, planOfActionId: String?, formTemplateId: String?): Flow<Form>

	suspend fun addDelegation(formId: String, delegation: Delegation): Form?

	suspend fun createForm(form: Form): Form?
	fun deleteForms(ids: Set<String>): Flow<DocIdentifier>

	suspend fun modifyForm(form: Form): Form?
	fun listByHcPartyAndParentId(hcPartyId: String, formId: String): Flow<Form>

	suspend fun addDelegations(formId: String, delegations: List<Delegation>): Form?
	fun solveConflicts(limit: Int? = null): Flow<IdAndRev>
	suspend fun getAllByLogicalUuid(formUuid: String): List<Form>
	suspend fun getAllByUniqueId(lid: String): List<Form>
}
