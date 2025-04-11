/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package org.taktik.icure.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.icure.asyncjacksonhttpclient.net.web.WebClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.taktik.couchdb.springframework.webclient.SpringWebfluxWebClient
import org.taktik.icure.asyncdao.CouchDbDispatcher
import org.taktik.icure.dao.CouchDbDispatcherProvider
import org.taktik.icure.properties.CouchDbLiteProperties
import org.taktik.icure.security.CouchDbCredentialsProvider
import org.taktik.icure.security.UsernamePassword
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider

@Configuration
@Profile("app")
class CouchDbLiteConfig(
	protected val couchDbProperties: CouchDbLiteProperties,
	private val couchDbDispatcherProvider: CouchDbDispatcherProvider
) {

	companion object {
		const val ICURE_PREFIX = "icure"
	}

	val webClientLogger: Log = LogFactory.getLog("org.taktik.icure.config.WebClient")

	val couchDbCredentialProvider: CouchDbCredentialsProvider = object : CouchDbCredentialsProvider {
		override fun getCredentials(): UsernamePassword {
			val username = couchDbProperties.username ?: throw IllegalStateException("CouchDB username is not set")
			val password = couchDbProperties.password ?: throw IllegalStateException("CouchDB password is not set")
			return UsernamePassword(username, password)
		}
	}

	@Bean
	fun httpClient(connectionProvider: ConnectionProvider) = SpringWebfluxWebClient(
		ReactorClientHttpConnector(HttpClient.create(connectionProvider).compress(true))
	) { xff ->
		xff.add(
			ExchangeFilterFunction.ofRequestProcessor { req ->
				if (webClientLogger.isDebugEnabled) {
					webClientLogger.debug("-> ${req.method().name()} ${req.url()}")
				}
				Mono.just(req)
			}
		)
	}

	@Bean
	fun patientCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper, couchDbProperties: CouchDbLiteProperties): CouchDbDispatcher {
		return couchDbDispatcherProvider.getDispatcher(httpClient, objectMapper, couchDbProperties.prefix, "patient", couchDbCredentialProvider, 1)
	}

	@Bean
	fun healthdataCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper, couchDbProperties: CouchDbLiteProperties): CouchDbDispatcher {
		return couchDbDispatcherProvider.getDispatcher(httpClient, objectMapper, couchDbProperties.prefix, "healthdata", couchDbCredentialProvider, 1)
	}

	@Bean
	fun baseCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper, couchDbProperties: CouchDbLiteProperties): CouchDbDispatcher {
		return couchDbDispatcherProvider.getDispatcher(httpClient, objectMapper, couchDbProperties.prefix, "base", couchDbCredentialProvider, 1)
	}

	@Bean
	fun systemCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper): CouchDbDispatcher {
		return couchDbDispatcherProvider.getDispatcher(httpClient, objectMapper, ICURE_PREFIX, "system", couchDbCredentialProvider, 1)
	}
}
