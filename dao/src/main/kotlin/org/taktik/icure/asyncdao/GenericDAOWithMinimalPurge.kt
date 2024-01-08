package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.couchdb.id.Identifiable
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation

/**
 * Adds more efficient purge methods to the generic DAO.
 * These can only be used for entities where there is no need to perform any operation before and/or after purging the
 * entity.
 */
interface GenericDAOWithMinimalPurge<T : Identifiable<String>> : GenericDAO<T> {
    /**
     * Purge an entity by id and rev only, without having to load the entire entity in memory.
     * Fails with an exception (after potentially emitting some entities) if the updated of at least an entity has failed.
     */
    fun purgeByIdAndRev(datastoreInformation: IDatastoreInformation, idsAndRevs: Collection<IdAndRev>): Flow<DocIdentifier>
}