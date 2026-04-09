package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.DeviceLogic
import org.taktik.icure.asyncservice.DeviceService
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.Device
import org.taktik.icure.entities.conflicts.ConflictResolutionResult
import org.taktik.icure.entities.conflicts.MergeResult

@Service
class DeviceServiceImpl(
	private val deviceLogic: DeviceLogic
) : DeviceService {
	override suspend fun createDevice(device: Device): Device = deviceLogic.createDevice(device)

	override fun createDevices(devices: List<Device>): Flow<Device> = deviceLogic.createDevices(devices)

	override suspend fun modifyDevice(device: Device): Device = deviceLogic.modifyDevice(device)

	override fun modifyDevices(devices: List<Device>): Flow<Device> = deviceLogic.modifyDevices(devices)

	override suspend fun getDevice(deviceId: String): Device? = deviceLogic.getDevice(deviceId)

	override fun getDevices(deviceIds: List<String>): Flow<Device> = deviceLogic.getDevices(deviceIds)

	@Suppress("DEPRECATION")
	@Deprecated("A DataOwner may now have multiple AES Keys. Use getAesExchangeKeysForDelegate instead")
	override suspend fun getHcPartyKeysForDelegate(deviceId: String): Map<String, String> = deviceLogic.getHcPartyKeysForDelegate(deviceId)

	override suspend fun getAesExchangeKeysForDelegate(healthcarePartyId: String): Map<String, Map<String, Map<String, String>>> = deviceLogic.getAesExchangeKeysForDelegate(healthcarePartyId)
	override fun deleteDevices(ids: List<IdAndRev>): Flow<Device> = deviceLogic.deleteEntities(ids)
	override suspend fun deleteDevice(id: String, rev: String?): Device = deviceLogic.deleteEntity(id, rev)
	override suspend fun purgeDevice(id: String, rev: String): DocIdentifier = deviceLogic.purgeEntity(id, rev)
	override fun purgeDevices(deviceIds: List<IdAndRev>): Flow<DocIdentifier> = deviceLogic.purgeEntities(deviceIds)

	override suspend fun undeleteDevice(id: String, rev: String): Device = deviceLogic.undeleteEntity(id, rev)
	override fun undeleteDevices(deviceIds: List<IdAndRev>): Flow<Device> = deviceLogic.undeleteEntities(deviceIds)

	override fun filterDevices(
		filter: FilterChain<Device>,
		limit: Int,
		startDocumentId: String?
	): Flow<ViewQueryResultEvent> = deviceLogic.filterDevices(filter, limit, startDocumentId)

	override fun getEntityIds(): Flow<String> = deviceLogic.getEntityIds()
	override fun matchDevicesBy(filter: AbstractFilter<Device>): Flow<String> = deviceLogic.matchEntitiesBy(filter)

	override fun getConflictingEntitiesIds(): Flow<String> = deviceLogic.getConflictingEntitiesIds()
	override fun getConflictsFor(entityId: String): Flow<Device> = deviceLogic.getConflictsFor(entityId)
	override suspend fun declareConflictWinner(
		entity: Device,
		conflictsToPurge: List<String>
	): ConflictResolutionResult<Device> {
		val conflicts = conflictsToPurge.mapNotNull { rev ->
			deviceLogic.getBypassingCache(entity.id, rev)
		}
		return deviceLogic.declareConflictWinner(entity, conflicts)
	}
	override fun solveConflicts(
		limit: Int?,
		ids: List<String>?
	): Flow<MergeResult> = deviceLogic.solveConflicts(limit, ids)
}