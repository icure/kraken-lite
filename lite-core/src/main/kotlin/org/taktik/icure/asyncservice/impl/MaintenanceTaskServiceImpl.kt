package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.MaintenanceTaskLogic
import org.taktik.icure.asyncservice.MaintenanceTaskService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.MaintenanceTask
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.entities.filters.AbstractCustomFilter
import org.taktik.icure.pagination.PaginationElement

@Service
class MaintenanceTaskServiceImpl(
	private val maintenanceTaskLogic: MaintenanceTaskLogic
) : MaintenanceTaskService {
	override fun deleteMaintenanceTasks(ids: List<IdAndRev>): Flow<MaintenanceTask> = maintenanceTaskLogic.deleteEntities(ids)
	override suspend fun deleteMaintenanceTask(id: String, rev: String?): MaintenanceTask = maintenanceTaskLogic.deleteEntity(id, rev)
	override suspend fun purgeMaintenanceTask(id: String, rev: String): DocIdentifier = maintenanceTaskLogic.purgeEntity(id, rev)
	override suspend fun undeleteMaintenanceTask(id: String, rev: String): MaintenanceTask = maintenanceTaskLogic.undeleteEntity(id, rev)
	override suspend fun modifyMaintenanceTask(entity: MaintenanceTask): MaintenanceTask = maintenanceTaskLogic.modifyEntity(entity)

	override suspend fun createMaintenanceTask(entity: MaintenanceTask): MaintenanceTask = maintenanceTaskLogic.createMaintenanceTask(entity)

	override fun modifyMaintenanceTasks(entities: Collection<MaintenanceTask>): Flow<MaintenanceTask> = maintenanceTaskLogic.modifyEntities(entities)

	override fun filterMaintenanceTasks(
		paginationOffset: PaginationOffset<Nothing>,
		filter: FilterChain<MaintenanceTask>
	): Flow<ViewQueryResultEvent> = maintenanceTaskLogic.filter(paginationOffset, filter)

	override suspend fun getMaintenanceTask(id: String): MaintenanceTask? = maintenanceTaskLogic.getEntity(id)
	override fun getMaintenanceTasks(ids: List<String>): Flow<MaintenanceTask> = maintenanceTaskLogic.getEntities(ids)

	override fun createMaintenanceTasks(entities: Collection<MaintenanceTask>): Flow<MaintenanceTask> = maintenanceTaskLogic.createEntities(entities)
	override fun matchMaintenanceTasksBy(filter: AbstractFilter<MaintenanceTask>): Flow<String> = maintenanceTaskLogic.matchEntitiesBy(filter)

	override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<MaintenanceTask>> = maintenanceTaskLogic.bulkShareOrUpdateMetadata(requests)

	// Kraken lite does not restrict access to custom views by data owner
	override suspend fun requestedDataOwnerIds(filter: AbstractCustomFilter): Set<String> = emptySet()

	override fun matchByCustomFilter(filter: AbstractCustomFilter): Flow<PaginationElement> = maintenanceTaskLogic.matchEntitiesByCustomFilter(filter)
}
