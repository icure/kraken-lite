package org.taktik.icure.asynclogic.impl

import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asyncdao.ICureDAO
import org.taktik.icure.asyncdao.Partitions
import org.taktik.icure.asynclogic.VersionLogic
import org.taktik.icure.asynclogic.datastore.DatastoreInstanceProvider
import org.taktik.icure.entities.ReplicationInfo
import org.taktik.icure.entities.embed.DatabaseSynchronization
import org.taktik.icure.properties.CouchDbPropertiesImpl

@Service
@Profile("app")
class ICureLiteLogicImpl(
    private val allDaos: List<GenericDAO<*>>,
    private val datastoreInstanceProvider: DatastoreInstanceProvider,
    private val iCureDAO: ICureDAO,
    couchDbProperties: CouchDbPropertiesImpl,
    passwordEncoder: PasswordEncoder,
    versionLogic: VersionLogic
) : AbstractICureLogic(couchDbProperties, iCureDAO, passwordEncoder, versionLogic, datastoreInstanceProvider) {
    override suspend fun updateDesignDocForCurrentUser(daoEntityName: String, warmup: Boolean) {
        allDaos
            .firstOrNull { dao: GenericDAO<*> -> dao.javaClass.simpleName.startsWith(daoEntityName + "DAO") }
            ?.let { dao: GenericDAO<*> ->
                val datastoreInformation = datastoreInstanceProvider.getInstanceAndGroup()
                dao.forceInitStandardDesignDocument(datastoreInformation, true, warmup)
                if (warmup) {
                    runCatching {
                        dao.warmupPartition(datastoreInformation, Partitions.All)
                    }
                }
            }
    }

    override suspend fun getReplicationInfo(): ReplicationInfo {
        val datastoreInformation = datastoreInstanceProvider.getInstanceAndGroup()
        val changes: Map<DatabaseSynchronization, Long> = iCureDAO.getPendingChanges(datastoreInformation)
        return changes.toList().fold(ReplicationInfo()) { r, (db, pending) ->
            r.copy(
                active = true,
                pendingFrom = if (db.source?.contains(dbInstanceUri.host) == true) ((r.pendingFrom ?: 0) + pending).toInt() else r.pendingFrom,
                pendingTo = if (db.source?.contains(dbInstanceUri.host) == true) ((r.pendingTo ?: 0) + pending).toInt() else r.pendingTo
            )
        }
    }

}
