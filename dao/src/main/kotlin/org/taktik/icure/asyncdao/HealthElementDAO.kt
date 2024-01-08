/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.embed.Identifier

interface HealthElementDAO : GenericDAO<HealthElement> {
	fun listHealthElementsByHcParty(datastoreInformation: IDatastoreInformation, hcPartyId: String): Flow<String>

	fun listHealthElementIdsByHcPartyAndSecretPatientKeys(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, secretPatientKeys: List<String>): Flow<String>

	fun listHealthElementsByHCPartyAndCodes(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, codeType: String, codeNumber: String): Flow<String>

	fun listHealthElementsByHCPartyAndTags(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, tagType: String, tagCode: String): Flow<String>

	fun listHealthElementsByHCPartyAndStatus(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, status: Int?): Flow<String>

	fun listHealthElementsIdsByHcPartyAndIdentifiers(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, identifiers: List<Identifier>): Flow<String>

	suspend fun getHealthElementByPlanOfActionId(datastoreInformation: IDatastoreInformation, planOfActionId: String): HealthElement?

	suspend fun getHealthElement(datastoreInformation: IDatastoreInformation, healthElementId: String): HealthElement?

	fun listHealthElementsByHCPartyAndSecretPatientKeys(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, secretPatientKeys: List<String>): Flow<HealthElement>

	fun listConflicts(datastoreInformation: IDatastoreInformation): Flow<HealthElement>

	fun findHealthElementsByIds(datastoreInformation: IDatastoreInformation, healthElementIds: Flow<String>): Flow<ViewQueryResultEvent>
}
