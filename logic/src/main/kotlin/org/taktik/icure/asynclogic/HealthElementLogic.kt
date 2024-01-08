/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.base.EntityWithSecureDelegationsLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Identifier

interface HealthElementLogic : EntityPersister<HealthElement, String>, EntityWithSecureDelegationsLogic<HealthElement> {
	suspend fun getHealthElement(healthElementId: String): HealthElement?
	fun getHealthElements(healthElementIds: Collection<String>): Flow<HealthElement>

	fun listHealthElementsByHcPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<HealthElement>
	fun listHealthElementIdsByHcPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<String>

	fun listHealthElementIdsByHcParty(hcpId: String): Flow<String>
	suspend fun listLatestHealthElementsByHcPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): List<HealthElement>
	fun listHealthElementIdsByHcPartyAndCodes(hcPartyId: String, codeType: String, codeNumber: String): Flow<String>
	fun listHealthElementIdsByHcPartyAndTags(hcPartyId: String, tagType: String, tagCode: String): Flow<String>
	fun listHealthElementsIdsByHcPartyAndIdentifiers(hcPartyId: String, identifiers: List<Identifier>): Flow<String>
	fun listHealthElementIdsByHcPartyAndStatus(hcPartyId: String, status: Int): Flow<String>
	fun deleteHealthElements(ids: Set<String>): Flow<DocIdentifier>

	suspend fun modifyHealthElement(healthElement: HealthElement): HealthElement?

	suspend fun addDelegation(healthElementId: String, delegation: Delegation): HealthElement?

	suspend fun addDelegations(healthElementId: String, delegations: List<Delegation>): HealthElement?

	fun solveConflicts(limit: Int? = null): Flow<IdAndRev>

	fun filter(
		paginationOffset: PaginationOffset<Nothing>,
		filter: FilterChain<HealthElement>
	): Flow<ViewQueryResultEvent>
}
