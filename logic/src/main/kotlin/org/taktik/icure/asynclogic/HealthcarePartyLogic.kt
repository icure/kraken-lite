/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.embed.Identifier

interface HealthcarePartyLogic : EntityPersister<HealthcareParty, String> {

	suspend fun getHealthcareParty(id: String): HealthcareParty?
	fun listHealthcarePartiesBy(searchString: String, offset: Int, limit: Int): Flow<HealthcareParty>

	@Deprecated(message = "A HCP may now have multiple AES Keys. Use getAesExchangeKeysForDelegate instead")
	suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String>

	suspend fun getAesExchangeKeysForDelegate(healthcarePartyId: String): Map<String, Map<String, Map<String, String>>>

	suspend fun modifyHealthcareParty(healthcareParty: HealthcareParty): HealthcareParty?
	fun deleteHealthcareParties(healthcarePartyIds: List<String>): Flow<DocIdentifier>

	suspend fun createHealthcareParty(healthcareParty: HealthcareParty): HealthcareParty?

	fun findHealthcarePartiesBy(offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>
	fun findHealthcarePartiesBy(fuzzyName: String, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>
	fun listHealthcarePartiesByNihii(nihii: String): Flow<HealthcareParty>
	fun listHealthcarePartiesBySsin(ssin: String): Flow<HealthcareParty>
	fun listHealthcarePartiesByName(name: String): Flow<HealthcareParty>

	suspend fun getPublicKey(healthcarePartyId: String): String?
	fun listHealthcarePartiesBySpecialityAndPostcode(type: String, spec: String, firstCode: String, lastCode: String): Flow<ViewQueryResultEvent>
	fun getHealthcareParties(ids: List<String>): Flow<HealthcareParty>
	fun findHealthcarePartiesBySsinOrNihii(searchValue: String, paginationOffset: PaginationOffset<String>, desc: Boolean): Flow<ViewQueryResultEvent>
	fun getHealthcarePartiesByParentId(parentId: String): Flow<HealthcareParty>
	suspend fun getHcpHierarchyIds(sender: HealthcareParty): HashSet<String>
	fun filterHealthcareParties(paginationOffset: PaginationOffset<Nothing>, filter: FilterChain<HealthcareParty>): Flow<ViewQueryResultEvent>
	fun listHealthcarePartyIdsByIdentifiers(hcpIdentifiers: List<Identifier>): Flow<String>
	fun listHealthcarePartyIdsByCode(codeType: String, codeCode: String?): Flow<String>
	fun listHealthcarePartyIdsByTag(tagType: String, tagCode: String?): Flow<String>
	fun listHealthcarePartyIdsByName(name: String, desc: Boolean = false): Flow<String>
}
