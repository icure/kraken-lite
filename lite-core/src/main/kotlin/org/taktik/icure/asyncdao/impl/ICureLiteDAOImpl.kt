package org.taktik.icure.asyncdao.impl

import io.icure.asyncjacksonhttpclient.net.web.WebClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.taktik.couchdb.Client
import org.taktik.icure.asyncdao.CouchDbDispatcher
import org.taktik.icure.asyncdao.ICureLiteDAO
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.security.CouchDbCredentialsProvider


@OptIn(ExperimentalCoroutinesApi::class)
class ICureLiteDAOImpl(
	httpClient: WebClient,
	couchDbCredentialsProvider: CouchDbCredentialsProvider,
	couchDbDispatcher: CouchDbDispatcher
) : ICureLiteDAO, ICureDAOImpl(httpClient, couchDbCredentialsProvider, couchDbDispatcher) {

	companion object {
		private const val COUCHDB_LOCAL_NODE = "nonode@nohost"
	}

	private suspend fun checkOnlyLocalNodeExists(client: Client) {
		val membership = client.membership()
		if (membership.allNodes.size != 1 || membership.allNodes.first() != COUCHDB_LOCAL_NODE) {
			throw IllegalStateException("Multiple nodes found on local installation")
		}
	}

	override suspend fun getCouchDbConfigProperty(datastoreInformation: IDatastoreInformation, section: String, key: String): String? {
		val client = couchDbDispatcher.getClient(datastoreInformation)
		checkOnlyLocalNodeExists(client)
		return client.getConfigOption(COUCHDB_LOCAL_NODE, section, key)
	}

	override suspend fun setCouchDbConfigProperty(datastoreInformation: IDatastoreInformation, section: String, key: String, newValue: String) {
		val client = couchDbDispatcher.getClient(datastoreInformation)
		checkOnlyLocalNodeExists(client)
		client.setConfigOption(COUCHDB_LOCAL_NODE, section, key, newValue)
	}

}