package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.taktik.couchdb.entity.ViewQuery
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.CouchDbDispatcher
import org.taktik.icure.asyncdao.EntityInfoDAO
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.entities.base.EntityInfo
import org.taktik.icure.services.external.rest.v2.dto.PaginatedDocumentKeyIdPair
import org.taktik.icure.services.external.rest.v2.utils.paginatedList

class EntityInfoDAOImpl(
    private val couchDbDispatcher: CouchDbDispatcher
) : EntityInfoDAO {
    override fun getEntitiesInfo(
        datastoreInformation: IDatastoreInformation,
        ids: Collection<String>
    ): Flow<EntityInfo> = flow {
        val client = couchDbDispatcher.getClient(datastoreInformation)
        var next: PaginatedDocumentKeyIdPair<*>? = null
        do {
            val viewQuery = ViewQuery()
                .allDocs()
                .includeDocs(true)
                .keys(ids)
                .ignoreNotFound(true)
                .limit(ids.size + 1)
                .startKey(next?.startKey)
                .startDocId(next?.startKeyDocId)
            val retrieved = client.queryViewIncludeDocsNoValue<String, EntityInfo>(viewQuery)
                .paginatedList<EntityInfo>(ids.size)
            next = retrieved.nextKeyPair
            retrieved.rows.forEach { emit(it) }
        } while (next != null)
    }
}