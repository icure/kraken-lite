package org.taktik.icure.asyncservice.impl

import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.ICureLiteLogic
import org.taktik.icure.asynclogic.ICureLogic
import org.taktik.icure.asyncservice.ICureLiteService
import org.taktik.icure.asyncservice.ICureSharedService
import org.taktik.icure.domain.IndexingInfo
import org.taktik.icure.entities.ReplicationInfo

@Service
class ICureLiteServiceImpl(
    private val iCureLogic: ICureLiteLogic
) : ICureSharedService, ICureLiteService {
    override suspend fun getReplicationInfo(): ReplicationInfo = iCureLogic.getReplicationInfo()

    override suspend fun getIndexingStatus(): IndexingInfo = iCureLogic.getIndexingStatus()

    override fun getVersion(): String = iCureLogic.getVersion()

    override suspend fun getProcessInfo(): String = iCureLogic.getProcessInfo()

    override suspend fun setLogLevel(logLevel: String, packageName: String): String = iCureLogic.setLogLevel(logLevel, packageName)

    override suspend fun updateDesignDocForCurrentUser(daoEntityName: String, warmup: Boolean) = iCureLogic.updateDesignDocForCurrentUser(daoEntityName, warmup)

    override suspend fun getCouchDbConfigProperty(section: String, key: String) = iCureLogic.getCouchDbConfigProperty(section, key)

    override suspend fun setCouchDbConfigProperty(section: String, key: String, newValue: String) = iCureLogic.setCouchDbConfigProperty(section, key, newValue)

    override suspend fun setKrakenLiteProperty(propertyName: String, value: Boolean) = iCureLogic.setKrakenLiteProperty(propertyName, value)

}