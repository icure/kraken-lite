package org.taktik.icure.asynclogic.datastore

interface DatastoreInstanceProvider {
	suspend fun getInstanceAndGroup(): IDatastoreInformation
}
