/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncservice

import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.AccessDeniedException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncservice.base.EntityWithSecureDelegationsService
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.embed.Delegation
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.exceptions.NotFoundRequestException

interface FormService : EntityWithSecureDelegationsService<Form> {
	suspend fun getForm(id: String): Form?
	fun getForms(selectedIds: Collection<String>): Flow<Form>
	fun listFormsByHCPartyAndPatient(hcPartyId: String, secretPatientKeys: List<String>, healthElementId: String?, planOfActionId: String?, formTemplateId: String?): Flow<Form>

	suspend fun addDelegation(formId: String, delegation: Delegation): Form?

	suspend fun createForm(form: Form): Form?

	/**
	 * Deletes a batch of [Form]s.
	 * If the user does not have the permission to delete an [Form] or the [Form] does not exist, then it
	 * is ignored without any warning.
	 *
	 * @param ids a [List] containing the ids of the [Form]s to delete.
	 * @return a [Flow] containing the [DocIdentifier]s of the [Form]s successfully deleted.
	 */
	fun deleteForms(ids: Set<String>): Flow<DocIdentifier>

	/**
	 * Deletes a [Form].
	 *
	 * @param id the id of the [Form] to delete.
	 * @return a [DocIdentifier] related to the [Form] if the operation completes successfully.
	 * @throws [AccessDeniedException] if the current user does not have the permission to delete the [Form].
	 * @throws [NotFoundRequestException] if an [Form] with the specified [id] does not exist.
	 */
	suspend fun deleteForm(id: String): DocIdentifier

	suspend fun modifyForm(form: Form): Form?
	fun listByHcPartyAndParentId(hcPartyId: String, formId: String): Flow<Form>

	suspend fun addDelegations(formId: String, delegations: List<Delegation>): Form?
	fun solveConflicts(limit: Int? = null): Flow<IdAndRev>
	suspend fun getAllByLogicalUuid(formUuid: String): List<Form>
	suspend fun getAllByUniqueId(lid: String): List<Form>

	/**
	 * Updates a batch of [Form]s. If any of the [Form]s in the batch specifies an invalid modification, then will be
	 * not applied but no error will be thrown.
	 * @param forms a [Collection] of updated [Form]s
	 * @return a [Flow] containing the successfully updated [Form]s.
	 */
	fun modifyForms(forms: Collection<Form>): Flow<Form>

	/**
	 * Creates [Form]s in batch. The user can perform this operation if they have the permission to create a single form.
	 * @param forms a [Collection] of [Form]s to create.
	 * @return a [Flow] containing the successfully created [Form]s
	 */
	fun createForms(forms: Collection<Form>): Flow<Form>
}
