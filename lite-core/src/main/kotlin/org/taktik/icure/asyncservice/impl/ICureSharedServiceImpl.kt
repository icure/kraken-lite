package org.taktik.icure.asyncservice.impl

import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.ICureLogic
import org.taktik.icure.asyncservice.ICureSharedService
import org.taktik.icure.domain.IndexingInfo
import org.taktik.icure.entities.ReplicationInfo

@Service
class ICureSharedServiceImpl(
    private val iCureLogic: ICureLogic
) : ICureSharedService {
    override suspend fun getReplicationInfo(): ReplicationInfo = iCureLogic.getReplicationInfo()

    override suspend fun getIndexingStatus(): IndexingInfo = iCureLogic.getIndexingStatus()

    override fun getVersion(): String = iCureLogic.getVersion()

    override fun getProcessInfo(): String = iCureLogic.getProcessInfo()

    override fun tokenCheck(token: String): String = iCureLogic.tokenCheck(token)

    override suspend fun setLogLevel(logLevel: String, packageName: String): String = iCureLogic.setLogLevel(logLevel, packageName)

    override suspend fun updateDesignDocForCurrentUser(daoEntityName: String, warmup: Boolean) = iCureLogic.updateDesignDocForCurrentUser(daoEntityName, warmup)
}