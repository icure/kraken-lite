/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Form

interface FormDAO : GenericDAO<Form> {
	fun listFormsByHcPartyPatient(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, secretPatientKeys: List<String>): Flow<Form>

	fun listFormsByHcPartyAndParentId(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, formId: String): Flow<Form>

	fun findForms(datastoreInformation: IDatastoreInformation, pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>

	fun listConflicts(datastoreInformation: IDatastoreInformation): Flow<Form>

	suspend fun getAllByLogicalUuid(datastoreInformation: IDatastoreInformation, formUuid: String): List<Form>

	suspend fun getAllByUniqueId(datastoreInformation: IDatastoreInformation, externalUuid: String): List<Form>
}
