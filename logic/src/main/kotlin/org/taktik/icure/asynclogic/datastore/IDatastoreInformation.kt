package org.taktik.icure.asynclogic.datastore

interface IDatastoreInformation{
    fun getFullIdFor(entityId: String): String
}
