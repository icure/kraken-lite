package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.DeviceLogic
import org.taktik.icure.asyncservice.DeviceService
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.Device

@Service
class DeviceServiceImpl(
    private val deviceLogic: DeviceLogic
) : DeviceService {
    override suspend fun createDevice(device: Device): Device? = deviceLogic.createDevice(device)

    override fun createDevices(devices: List<Device>): Flow<Device> = deviceLogic.createDevices(devices)

    override suspend fun modifyDevice(device: Device): Device? = deviceLogic.modifyDevice(device)

    override fun modifyDevices(devices: List<Device>): Flow<Device> = deviceLogic.modifyDevices(devices)

    override suspend fun getDevice(deviceId: String): Device? = deviceLogic.getDevice(deviceId)

    override fun getDevices(deviceIds: List<String>): Flow<Device> = deviceLogic.getDevices(deviceIds)

    @Suppress("DEPRECATION")
    @Deprecated("A DataOwner may now have multiple AES Keys. Use getAesExchangeKeysForDelegate instead")
    override suspend fun getHcPartyKeysForDelegate(deviceId: String): Map<String, String> = deviceLogic.getHcPartyKeysForDelegate(deviceId)

    override suspend fun getAesExchangeKeysForDelegate(healthcarePartyId: String): Map<String, Map<String, Map<String, String>>> = deviceLogic.getAesExchangeKeysForDelegate(healthcarePartyId)

    override suspend fun deleteDevice(id: String): DocIdentifier? = deviceLogic.deleteDevice(id)

    override fun deleteDevices(ids: Collection<String>): Flow<DocIdentifier> = deviceLogic.deleteDevices(ids)

    override fun filterDevices(
        filter: FilterChain<Device>,
        limit: Int,
        startDocumentId: String?
    ): Flow<ViewQueryResultEvent> = deviceLogic.filterDevices(filter, limit, startDocumentId)

    override fun getEntityIds(): Flow<String> = deviceLogic.getEntityIds()
    override fun matchDevicesBy(filter: AbstractFilter<Device>): Flow<String> = deviceLogic.matchEntitiesBy(filter)
}