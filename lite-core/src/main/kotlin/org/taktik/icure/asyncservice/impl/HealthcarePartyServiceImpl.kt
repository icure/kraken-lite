package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asyncservice.HealthcarePartyService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.pagination.PaginationElement

@Service
class HealthcarePartyServiceImpl(
    private val healthcarePartyLogic: HealthcarePartyLogic
) : HealthcarePartyService {
    override suspend fun getHealthcareParty(id: String): HealthcareParty? = healthcarePartyLogic.getHealthcareParty(id)

    override fun listHealthcarePartiesBy(searchString: String, offset: Int, limit: Int): Flow<HealthcareParty> =
        healthcarePartyLogic.listHealthcarePartiesBy(searchString, offset, limit)

    @Deprecated("A HCP may now have multiple AES Keys. Use getAesExchangeKeysForDelegate instead")
    @Suppress("DEPRECATION")
    override suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String> =
        healthcarePartyLogic.getHcPartyKeysForDelegate(healthcarePartyId)

    override suspend fun getAesExchangeKeysForDelegate(healthcarePartyId: String): Map<String, Map<String, Map<String, String>>> =
        healthcarePartyLogic.getAesExchangeKeysForDelegate(healthcarePartyId)

    override suspend fun modifyHealthcareParty(healthcareParty: HealthcareParty): HealthcareParty? =
        healthcarePartyLogic.modifyHealthcareParty(healthcareParty)

    override fun deleteHealthcareParties(healthcarePartyIds: List<String>): Flow<DocIdentifier> =
        healthcarePartyLogic.deleteHealthcareParties(healthcarePartyIds)

    override suspend fun deleteHealthcareParty(healthcarePartyId: String): DocIdentifier =
        healthcarePartyLogic.deleteHealthcareParties(
            listOf(healthcarePartyId)
        ).single()

    override suspend fun createHealthcareParty(healthcareParty: HealthcareParty): HealthcareParty? = healthcarePartyLogic.createHealthcareParty(healthcareParty)

    override fun findHealthcarePartiesBy(offset: PaginationOffset<String>, desc: Boolean?): Flow<PaginationElement> = healthcarePartyLogic.findHealthcarePartiesBy(offset, desc)

    override fun findHealthcarePartiesBy(
        fuzzyName: String,
        offset: PaginationOffset<String>,
        desc: Boolean?
    ): Flow<PaginationElement> = healthcarePartyLogic.findHealthcarePartiesBy(fuzzyName, offset, desc)

    override fun listHealthcarePartiesByNihii(nihii: String): Flow<HealthcareParty>  = healthcarePartyLogic.listHealthcarePartiesByNihii(nihii)

    override fun listHealthcarePartiesBySsin(ssin: String): Flow<HealthcareParty> = healthcarePartyLogic.listHealthcarePartiesBySsin(ssin)

    override fun listHealthcarePartiesByName(name: String): Flow<HealthcareParty> = healthcarePartyLogic.listHealthcarePartiesByName(name)

    override suspend fun getPublicKey(healthcarePartyId: String): String? = healthcarePartyLogic.getPublicKey(healthcarePartyId)
    override fun listHealthcarePartiesBySpecialityAndPostcode(
        type: String,
        spec: String,
        firstCode: String,
        lastCode: String,
        offset: PaginationOffset<ComplexKey>
    ): Flow<PaginationElement> = healthcarePartyLogic.listHealthcarePartiesBySpecialityAndPostcode(type, spec, firstCode, lastCode, offset)

    override fun getHealthcareParties(ids: List<String>): Flow<HealthcareParty> = healthcarePartyLogic.getHealthcareParties(ids)

    override fun findHealthcarePartiesBySsinOrNihii(
        searchValue: String,
        paginationOffset: PaginationOffset<String>,
        desc: Boolean
    ): Flow<PaginationElement> = healthcarePartyLogic.findHealthcarePartiesBySsinOrNihii(searchValue, paginationOffset, desc)

    override fun getHealthcarePartiesByParentId(parentId: String): Flow<HealthcareParty> = healthcarePartyLogic.getHealthcarePartiesByParentId(parentId)

    override suspend fun getHcpHierarchyIds(sender: HealthcareParty): HashSet<String> = healthcarePartyLogic.getHcpHierarchyIds(sender)

    override fun filterHealthcareParties(
        paginationOffset: PaginationOffset<Nothing>,
        filter: FilterChain<HealthcareParty>
    ): Flow<ViewQueryResultEvent> = healthcarePartyLogic.filterHealthcareParties(paginationOffset, filter)

    override fun modifyHealthcareParties(entities: Collection<HealthcareParty>): Flow<HealthcareParty> = healthcarePartyLogic.modifyEntities(entities)
    override fun matchHealthcarePartiesBy(filter: AbstractFilter<HealthcareParty>): Flow<String> = healthcarePartyLogic.matchEntitiesBy(filter)
}