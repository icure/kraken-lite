package org.taktik.icure.asynclogic.datastore.impl

import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import java.net.URI

data class LocalDatastoreInformation(
    val dbInstanceUrl: URI,
) : IDatastoreInformation {
    override fun getFullIdFor(entityId: String): String {
        return entityId
    }
}
