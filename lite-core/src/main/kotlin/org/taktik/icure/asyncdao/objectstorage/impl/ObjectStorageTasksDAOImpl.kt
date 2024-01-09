package org.taktik.icure.asyncdao.objectstorage.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.dao.DesignDocumentProvider
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageTasksDAO
import org.taktik.icure.asynclogic.datastore.DatastoreInstanceProvider
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.objectstorage.ObjectStorageTask

@Repository
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.objectstorage.ObjectStorageTask' && !doc.deleted) emit( null, doc._id )}")
class ObjectStorageTasksDAOImpl(
	@Qualifier("systemCouchDbDispatcher") systemCouchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator,
	datastoreInstanceProvider: DatastoreInstanceProvider,
	designDocumentProvider: DesignDocumentProvider
) : InternalDAOImpl<ObjectStorageTask>(
		ObjectStorageTask::class.java,
		systemCouchDbDispatcher,
		idGenerator,
		datastoreInstanceProvider,
		designDocumentProvider
	),
	ObjectStorageTasksDAO
{
	companion object {
		private const val BY_ENTITY_CLASS_ENTITY_ID_ATTACHMENT_ID = "by_entityclass_entityid_attachmentid"
		private const val BY_ENTITY_CLASS = "by_entityclass"
	}

	@View(name = BY_ENTITY_CLASS_ENTITY_ID_ATTACHMENT_ID, map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.objectstorage.ObjectStorageTask' && !doc.deleted) emit([doc.entityClassName, doc.entityId, doc.attachmentId], doc._id) }")
	override fun findRelatedTasks(task: ObjectStorageTask): Flow<ObjectStorageTask> = flow {
		val client = couchDbDispatcher.getClient(datastoreInstanceProvider.getInstanceAndGroup())
		val viewQuery = createQuery(BY_ENTITY_CLASS_ENTITY_ID_ATTACHMENT_ID)
			.key(ComplexKey.of(task.entityClassName, task.entityId, task.attachmentId))
			.includeDocs(true)
		emitAll(client.queryViewIncludeDocsNoValue<ComplexKey, ObjectStorageTask>(viewQuery).map { it.doc })
	}

	@View(name = BY_ENTITY_CLASS, map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.objectstorage.ObjectStorageTask' && !doc.deleted) emit(doc.entityClassName, doc._id) }")
	override fun <T : HasDataAttachments<T>> findTasksForEntities(entityClass: Class<T>) = flow {
		val client = couchDbDispatcher.getClient(datastoreInstanceProvider.getInstanceAndGroup())
		val viewQuery = createQuery(BY_ENTITY_CLASS)
			.key(entityClass.simpleName)
			.includeDocs(true)
		emitAll(client.queryViewIncludeDocsNoValue<String, ObjectStorageTask>(viewQuery).map { it.doc })
	}
}
