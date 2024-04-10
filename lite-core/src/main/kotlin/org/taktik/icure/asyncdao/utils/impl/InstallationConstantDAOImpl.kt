package org.taktik.icure.asyncdao.utils.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.dao.DesignDocumentProvider
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.asyncdao.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.utils.InstallationConstantDAO
import org.taktik.icure.asynclogic.datastore.DatastoreInstanceProvider
import org.taktik.icure.entities.utils.InstallationConstant

@Service
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.utils.InstallationConstant' && !doc.deleted) emit( null, doc._id )}")
class InstallationConstantDAOImpl(
	@Qualifier("systemCouchDbDispatcher") systemCouchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator,
	datastoreInstanceProvider: DatastoreInstanceProvider,
	designDocumentProvider: DesignDocumentProvider
) : InternalDAOImpl<InstallationConstant>(
	InstallationConstant::class.java,
	systemCouchDbDispatcher,
	idGenerator,
	datastoreInstanceProvider,
	designDocumentProvider
), InstallationConstantDAO
