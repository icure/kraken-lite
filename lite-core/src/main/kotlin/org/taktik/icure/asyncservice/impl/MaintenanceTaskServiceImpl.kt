package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.MaintenanceTaskLogic
import org.taktik.icure.asyncservice.MaintenanceTaskService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.MaintenanceTask
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult

@Service
class MaintenanceTaskServiceImpl(
    private val maintenanceTaskLogic: MaintenanceTaskLogic
) : MaintenanceTaskService {
    override fun listMaintenanceTasksByHcPartyAndIdentifier(
        healthcarePartyId: String,
        identifiers: List<Identifier>
    ): Flow<String> = maintenanceTaskLogic.listMaintenanceTasksByHcPartyAndIdentifier(healthcarePartyId, identifiers)

    override fun listMaintenanceTasksByHcPartyAndType(
        healthcarePartyId: String,
        type: String,
        startDate: Long?,
        endDate: Long?
    ): Flow<String> = maintenanceTaskLogic.listMaintenanceTasksByHcPartyAndType(healthcarePartyId, type, startDate, endDate)

    override fun listMaintenanceTasksAfterDate(healthcarePartyId: String, date: Long): Flow<String> = maintenanceTaskLogic.listMaintenanceTasksAfterDate(healthcarePartyId, date)

    override fun deleteMaintenanceTasks(ids: Collection<String>): Flow<DocIdentifier> = maintenanceTaskLogic.deleteEntities(ids)

    override suspend fun deleteMaintenanceTask(maintenanceTaskId: String): DocIdentifier = maintenanceTaskLogic.deleteEntities(
        listOf(maintenanceTaskId)).single()

    override suspend fun modifyMaintenanceTask(entity: MaintenanceTask): MaintenanceTask? = maintenanceTaskLogic.modifyEntities(listOf(entity)).single()

    override suspend fun createMaintenanceTask(entity: MaintenanceTask): MaintenanceTask? = maintenanceTaskLogic.createEntities(listOf(entity)).single()

    override fun modifyMaintenanceTasks(entities: Collection<MaintenanceTask>): Flow<MaintenanceTask> = maintenanceTaskLogic.modifyEntities(entities)

    override fun filterMaintenanceTasks(
        paginationOffset: PaginationOffset<Nothing>,
        filter: FilterChain<MaintenanceTask>
    ): Flow<ViewQueryResultEvent> = maintenanceTaskLogic.filter(paginationOffset, filter)

    override suspend fun getMaintenanceTask(id: String): MaintenanceTask? = maintenanceTaskLogic.getEntity(id)
    override fun getMaintenanceTasks(ids: List<String>): Flow<MaintenanceTask> = maintenanceTaskLogic.getEntities(ids)

    override fun createMaintenanceTasks(entities: Collection<MaintenanceTask>): Flow<MaintenanceTask> = maintenanceTaskLogic.createEntities(entities)

    override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<MaintenanceTask>> = maintenanceTaskLogic.bulkShareOrUpdateMetadata(requests)
}
