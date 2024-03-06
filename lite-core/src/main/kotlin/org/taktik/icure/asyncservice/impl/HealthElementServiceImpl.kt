package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asyncservice.HealthElementService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.pagination.PaginationElement

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

    override fun listHealthElementsByHcPartyAndSecretPatientKeys(
        hcPartyId: String,
        secretPatientKeys: List<String>
    ): Flow<HealthElement> =
        healthElementLogic.listHealthElementsByHcPartyAndSecretPatientKeys(hcPartyId, secretPatientKeys)

    override fun listHealthElementsByHCPartyIdAndSecretPatientKey(
        hcPartyId: String,
        secretPatientKey: String,
        offset: PaginationOffset<ComplexKey>
    ): Flow<PaginationElement> = healthElementLogic.listHealthElementsByHCPartyIdAndSecretPatientKey(hcPartyId, secretPatientKey, offset)

    override fun listHealthElementIdsByHcPartyAndSecretPatientKeys(
        hcPartyId: String,
        secretPatientKeys: List<String>
    ): Flow<String> = healthElementLogic.listHealthElementIdsByHcPartyAndSecretPatientKeys(hcPartyId, secretPatientKeys)

    override fun listHealthElementIdsByHcParty(hcpId: String): Flow<String> =
        healthElementLogic.listHealthElementIdsByHcParty(hcpId)

    override suspend fun listLatestHealthElementsByHcPartyAndSecretPatientKeys(
        hcPartyId: String,
        secretPatientKeys: List<String>
    ): List<HealthElement> =
        healthElementLogic.listLatestHealthElementsByHcPartyAndSecretPatientKeys(hcPartyId, secretPatientKeys)

    override fun listHealthElementIdsByHcPartyAndCodes(
        hcPartyId: String,
        codeType: String,
        codeNumber: String
    ): Flow<String> = healthElementLogic.listHealthElementIdsByHcPartyAndCodes(hcPartyId, codeType, codeNumber)

    override fun listHealthElementIdsByHcPartyAndTags(
        hcPartyId: String,
        tagType: String,
        tagCode: String
    ): Flow<String> = healthElementLogic.listHealthElementIdsByHcPartyAndTags(hcPartyId, tagType, tagCode)

    override fun listHealthElementsIdsByHcPartyAndIdentifiers(
        hcPartyId: String,
        identifiers: List<Identifier>
    ): Flow<String> = healthElementLogic.listHealthElementsIdsByHcPartyAndIdentifiers(hcPartyId, identifiers)

    override fun listHealthElementIdsByHcPartyAndStatus(hcPartyId: String, status: Int): Flow<String> =
        healthElementLogic.listHealthElementIdsByHcPartyAndStatus(hcPartyId, status)

    override fun deleteHealthElements(ids: Set<String>): Flow<DocIdentifier> = healthElementLogic.deleteEntities(ids)

    override suspend fun deleteHealthElement(id: String): DocIdentifier =
        checkNotNull(deleteHealthElements(setOf(id)).single()) {
            "HealthElement delete returned null from logic"
        }

    override suspend fun modifyHealthElement(healthElement: HealthElement): HealthElement? =
        checkNotNull(healthElementLogic.modifyEntities(flowOf(healthElement)).singleOrNull()) {
            "HealthElement modify returned null from logic"
        }

    override suspend fun addDelegations(healthElementId: String, delegations: List<Delegation>): HealthElement? =
        healthElementLogic.addDelegations(healthElementId, delegations)

    override fun solveConflicts(limit: Int?): Flow<IdAndRev> = healthElementLogic.solveConflicts(limit)

    override fun filter(
        paginationOffset: PaginationOffset<Nothing>,
        filter: FilterChain<HealthElement>
    ): Flow<ViewQueryResultEvent> = healthElementLogic.filter(paginationOffset, filter)

    override fun modifyEntities(entities: Flow<HealthElement>): Flow<HealthElement> =
        healthElementLogic.modifyEntities(entities)

    override fun createEntities(entities: Flow<HealthElement>): Flow<HealthElement> =
        healthElementLogic.createEntities(entities)

    override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<HealthElement>> =
        healthElementLogic.bulkShareOrUpdateMetadata(requests)
}