package org.taktik.icure.asyncdao.components

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.taktik.couchdb.entity.ActiveTask
import org.taktik.icure.asyncdao.CouchDbDispatcher
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation

@Service
@Profile("app")
class SimpleActiveTasksProvider(
	@Qualifier("baseCouchDbDispatcher") protected val couchDbDispatcher: CouchDbDispatcher,
) : ActiveTasksProvider {
	override suspend fun getActiveTasks(datastoreInformation: IDatastoreInformation): List<ActiveTask> =
		couchDbDispatcher.getClient(datastoreInformation).activeTasks()
}