package org.taktik.icure.asyncdao

import com.fasterxml.jackson.databind.ObjectMapper
import io.icure.asyncjacksonhttpclient.net.web.WebClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.stereotype.Component
import org.taktik.icure.dao.CouchDbDispatcherProvider
import org.taktik.icure.security.CouchDbCredentialsProvider

@Component
class LiteCouchDbDispatcherProvider: CouchDbDispatcherProvider {
	@OptIn(ExperimentalCoroutinesApi::class)
	override fun getDispatcher(
		httpClient: WebClient,
		objectMapper: ObjectMapper,
		prefix: String,
		dbFamily: String,
		couchDbCredentialsProvider: CouchDbCredentialsProvider,
		createdReplicasIfNotExists: Int,
	): CouchDbDispatcherImpl {
		val userNameAndPassword = couchDbCredentialsProvider.getCredentials()
		return CouchDbDispatcherImpl(
			httpClient,
			objectMapper,
			prefix,
			dbFamily,
			userNameAndPassword.username,
			userNameAndPassword.password,
			createdReplicasIfNotExists
		)
	}
}
