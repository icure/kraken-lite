package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.singleOrNull
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asyncservice.HealthElementService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult

@Suppress("DEPRECATION")
@Service
class HealthElementServiceImpl(
    private val healthElementLogic: HealthElementLogic
) : HealthElementService {
    override suspend fun createHealthElement(healthElement: HealthElement): HealthElement? =
        healthElementLogic.createEntities(
            flowOf(healthElement)
        ).singleOrNull()

    override suspend fun getHealthElement(healthElementId: String): HealthElement? =
        healthElementLogic.getHealthElement(healthElementId)

    override fun getHealthElements(healthElementIds: Collection<String>): Flow<HealthElement> =
        healthElementLogic.getHealthElements(healthElementIds)

    @Suppress("DEPRECATION")
    @Deprecated("This method cannot include results with secure delegations, use listHealthElementIdsByDataOwnerPatientOpeningDate instead")
    override fun listHealthElementsByHcPartyAndSecretPatientKeys(
        hcPartyId: String,
        secretPatientKeys: List<String>
    ): Flow<HealthElement> =
        healthElementLogic.listHealthElementsByHcPartyAndSecretPatientKeys(hcPartyId, secretPatientKeys)

    override fun listHealthElementIdsByDataOwnerPatientOpeningDate(
        dataOwnerId: String,
        secretForeignKeys: Set<String>,
        startDate: Long?,
        endDate: Long?,
        descending: Boolean
    ): Flow<String> = healthElementLogic.listHealthElementIdsByDataOwnerPatientOpeningDate(dataOwnerId, secretForeignKeys, startDate, endDate, descending)

    @Deprecated("This method is inefficient for high volumes of keys, use listHealthElementIdsByDataOwnerPatientOpeningDate instead")
    override suspend fun listLatestHealthElementsByHcPartyAndSecretPatientKeys(
        hcPartyId: String,
        secretPatientKeys: List<String>
    ): List<HealthElement> =
        healthElementLogic.listLatestHealthElementsByHcPartyAndSecretPatientKeys(hcPartyId, secretPatientKeys)

    override fun deleteHealthElements(ids: List<IdAndRev>): Flow<HealthElement> = healthElementLogic.deleteEntities(ids)
    override suspend fun deleteHealthElement(id: String, rev: String?): HealthElement = healthElementLogic.deleteEntity(id, rev)
    override suspend fun purgeHealthElement(id: String, rev: String): DocIdentifier = healthElementLogic.purgeEntity(id, rev)
    override suspend fun undeleteHealthElement(id: String, rev: String): HealthElement = healthElementLogic.undeleteEntity(id, rev)
    override suspend fun modifyHealthElement(healthElement: HealthElement): HealthElement? =
        checkNotNull(healthElementLogic.modifyEntities(flowOf(healthElement)).singleOrNull()) {
            "HealthElement modify returned null from logic"
        }

    override suspend fun addDelegations(healthElementId: String, delegations: List<Delegation>): HealthElement? =
        healthElementLogic.addDelegations(healthElementId, delegations)

    override fun solveConflicts(limit: Int?, ids: List<String>?) = healthElementLogic.solveConflicts(limit, ids)


    override fun filter(
        paginationOffset: PaginationOffset<Nothing>,
        filter: FilterChain<HealthElement>
    ): Flow<ViewQueryResultEvent> = healthElementLogic.filter(paginationOffset, filter)

    override fun matchHealthElementsBy(filter: AbstractFilter<HealthElement>): Flow<String> = healthElementLogic.matchEntitiesBy(filter)

    override fun modifyEntities(entities: Flow<HealthElement>): Flow<HealthElement> =
        healthElementLogic.modifyEntities(entities)

    override fun createEntities(entities: Flow<HealthElement>): Flow<HealthElement> =
        healthElementLogic.createEntities(entities)

    override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<HealthElement>> =
        healthElementLogic.bulkShareOrUpdateMetadata(requests)
}
