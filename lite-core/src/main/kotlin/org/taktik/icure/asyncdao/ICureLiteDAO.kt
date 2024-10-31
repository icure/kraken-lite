package org.taktik.icure.asyncdao

import org.taktik.icure.asynclogic.datastore.IDatastoreInformation

interface ICureLiteDAO : ICureDAO {
	suspend fun getCouchDbConfigProperty(datastoreInformation: IDatastoreInformation, section: String, key: String): String?
	suspend fun setCouchDbConfigProperty(datastoreInformation: IDatastoreInformation, section: String, key: String, newValue: String)
}