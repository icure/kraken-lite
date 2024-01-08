/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncservice

import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.AccessDeniedException
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.User
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.exceptions.NotFoundRequestException

interface HealthcarePartyService {
	suspend fun getHealthcareParty(id: String): HealthcareParty?
	fun listHealthcarePartiesBy(searchString: String, offset: Int, limit: Int): Flow<HealthcareParty>

	@Deprecated(message = "A HCP may now have multiple AES Keys. Use getAesExchangeKeysForDelegate instead")
	suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String>

	suspend fun getAesExchangeKeysForDelegate(healthcarePartyId: String): Map<String, Map<String, Map<String, String>>>

	suspend fun modifyHealthcareParty(healthcareParty: HealthcareParty): HealthcareParty?

	/**
	 * Deletes a batch of [HealthcareParty]s.
	 * If the user does not have the permission to delete an [HealthcareParty] or the [HealthcareParty] does not exist, then it
	 * is ignored without any warning.
	 *
	 * @param healthcarePartyIds a [List] containing the ids of the [HealthcareParty]s to delete.
	 * @return a [Flow] containing the [DocIdentifier]s of the [HealthcareParty]s successfully deleted.
	 */
	fun deleteHealthcareParties(healthcarePartyIds: List<String>): Flow<DocIdentifier>

	/**
	 * Deletes a [HealthcareParty].
	 *
	 * @param healthcarePartyId the id of the [HealthcareParty] to delete.
	 * @return a [DocIdentifier] related to the [HealthcareParty] if the operation completes successfully.
	 * @throws [AccessDeniedException] if the current user does not have the permission to delete the [HealthcareParty]
	 * in the specified group.
	 * @throws [NotFoundRequestException] if an [HealthcareParty] with the specified [healthcarePartyId] does not exist.
	 */
	suspend fun deleteHealthcareParty(healthcarePartyId: String): DocIdentifier

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
	fun modifyHealthcareParties(entities: Collection<HealthcareParty>): Flow<HealthcareParty>
}
