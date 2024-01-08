/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.embed.Identifier

interface HealthcarePartyDAO : GenericDAO<HealthcareParty> {
	fun listHealthcarePartiesByNihii(datastoreInformation: IDatastoreInformation, nihii: String?): Flow<HealthcareParty>

	fun listHealthcarePartiesBySsin(datastoreInformation: IDatastoreInformation, ssin: String): Flow<HealthcareParty>

	fun listHealthcarePartiesBySpecialityAndPostcode(datastoreInformation: IDatastoreInformation, type: String, spec: String, firstCode: String, lastCode: String): Flow<ViewQueryResultEvent>

	fun findHealthCareParties(datastoreInformation: IDatastoreInformation, pagination: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>

	fun listHealthcarePartiesByName(datastoreInformation: IDatastoreInformation, name: String): Flow<HealthcareParty>

	fun findHealthcarePartiesBySsinOrNihii(datastoreInformation: IDatastoreInformation, searchValue: String?, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>

	fun findHealthcarePartiesByHcPartyNameContainsFuzzy(datastoreInformation: IDatastoreInformation, searchString: String?, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>

	fun listHealthcareParties(datastoreInformation: IDatastoreInformation, searchString: String, offset: Int, limit: Int): Flow<HealthcareParty>

	@Deprecated(message = "A HCP may now have multiple AES Keys. Use getAesExchangeKeysForDelegate instead")
	suspend fun getHcPartyKeysForDelegate(datastoreInformation: IDatastoreInformation, healthcarePartyId: String): Map<String, String>

	suspend fun getAesExchangeKeysForDelegate(datastoreInformation: IDatastoreInformation, healthcarePartyId: String): Map<String, Map<String, Map<String, String>>>

	fun listHealthcarePartiesByParentId(datastoreInformation: IDatastoreInformation, parentId: String): Flow<HealthcareParty>

	fun findHealthcarePartiesByIds(datastoreInformation: IDatastoreInformation, hcpIds: Flow<String>): Flow<ViewQueryResultEvent>
	fun listHealthcarePartyIdsByIdentifiers(datastoreInformation: IDatastoreInformation, hcpIdentifiers: List<Identifier>): Flow<String>
	fun listHealthcarePartyIdsByCode(datastoreInformation: IDatastoreInformation, codeType: String, codeCode: String?): Flow<String>
	fun listHealthcarePartyIdsByTag(datastoreInformation: IDatastoreInformation, tagType: String, tagCode: String?): Flow<String>
	fun listHealthcarePartyIdsByName(datastoreInformation: IDatastoreInformation, name: String, desc: Boolean = false): Flow<String>
}
