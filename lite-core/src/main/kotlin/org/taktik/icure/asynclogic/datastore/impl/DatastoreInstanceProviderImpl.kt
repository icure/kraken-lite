package org.taktik.icure.asynclogic.datastore.impl

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.taktik.icure.datastore.DatastoreInstanceProvider
import org.taktik.icure.datastore.IDatastoreInformation
import org.taktik.icure.properties.CouchDbPropertiesImpl
import java.net.URI

@Service
@Profile("app")
class DatastoreInstanceProviderImpl(
	private val couchDbPropertiesImpl: CouchDbPropertiesImpl
): DatastoreInstanceProvider {
	override suspend fun getInstanceAndGroup(): IDatastoreInformation =
		LocalDatastoreInformation(URI(couchDbPropertiesImpl.url))
}