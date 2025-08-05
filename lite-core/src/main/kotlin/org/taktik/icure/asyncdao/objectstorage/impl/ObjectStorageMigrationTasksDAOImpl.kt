package org.taktik.icure.asyncdao.objectstorage.impl

import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.dao.DesignDocumentProvider
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageMigrationTasksDAO
import org.taktik.icure.datastore.DatastoreInstanceProvider
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask

@Repository
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask' && !doc.deleted) emit( null, doc._id )}")
class ObjectStorageMigrationTasksDAOImpl(
	systemCouchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator,
	datastoreInstanceProvider: DatastoreInstanceProvider,
	designDocumentProvider: DesignDocumentProvider
) : InternalDAOImpl<ObjectStorageMigrationTask>(
		ObjectStorageMigrationTask::class.java,
		systemCouchDbDispatcher,
		idGenerator,
		datastoreInstanceProvider,
		designDocumentProvider
	),
	ObjectStorageMigrationTasksDAO
{
	companion object {
		private const val BY_ENTITY_CLASS = "by_entityclass"
	}

	@View(name = BY_ENTITY_CLASS, map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask' && !doc.deleted) emit(doc.entityClassName, doc._id) }")
	override fun <T : HasDataAttachments<T>> findTasksForEntities(entityClass: Class<T>) = flow {
		val client = couchDbDispatcher.getClient(datastoreInstanceProvider.getInstanceAndGroup())
		val viewQuery = createQuery(BY_ENTITY_CLASS)
			.key(entityClass.simpleName)
			.includeDocs(true)
		emitAll(client.queryViewIncludeDocsNoValue<String, ObjectStorageMigrationTask>(viewQuery).map { it.doc })
	}
}
