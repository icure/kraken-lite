/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.icure.asyncjacksonhttpclient.net.web.WebClient
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.taktik.couchdb.springframework.webclient.SpringWebfluxWebClient
import org.taktik.icure.asyncdao.CouchDbDispatcher
import org.taktik.icure.asyncdao.CouchDbDispatcherImpl
import org.taktik.icure.properties.CouchDbLiteProperties
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider

@Configuration
@Profile("app")
class CouchDbLiteConfig(
	protected val couchDbProperties: CouchDbLiteProperties,
) {

	companion object {
		const val ICURE_PREFIX = "icure"
	}

	val webClientLogger: Log = LogFactory.getLog("org.taktik.icure.config.WebClient")

	@Bean
	fun httpClient(connectionProvider: ConnectionProvider) = SpringWebfluxWebClient(
		ReactorClientHttpConnector(HttpClient.create(connectionProvider).compress(true))
	) { xff ->
		xff.add(
			ExchangeFilterFunction.ofRequestProcessor { req ->
				if (webClientLogger.isDebugEnabled) {
					webClientLogger.debug("-> ${req.method().name} ${req.url()}")
				}
				Mono.just(req)
			}
		)
	}

	@Bean
	fun patientCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper, couchDbProperties: CouchDbLiteProperties): CouchDbDispatcher {
		return CouchDbDispatcherImpl(httpClient, objectMapper, couchDbProperties.prefix, "patient", couchDbProperties.username!!, couchDbProperties.password!!, 1)
	}

	@Bean
	fun healthdataCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper, couchDbProperties: CouchDbLiteProperties): CouchDbDispatcher {
		return CouchDbDispatcherImpl(httpClient, objectMapper, couchDbProperties.prefix, "healthdata", couchDbProperties.username!!, couchDbProperties.password!!, 1)
	}

	@Bean
	fun baseCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper, couchDbProperties: CouchDbLiteProperties): CouchDbDispatcher {
		return CouchDbDispatcherImpl(httpClient, objectMapper, couchDbProperties.prefix, "base", couchDbProperties.username!!, couchDbProperties.password!!, 1)
	}

	@Bean
	fun drugCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper, couchDbProperties: CouchDbLiteProperties): CouchDbDispatcher {
		return CouchDbDispatcherImpl(httpClient, objectMapper, couchDbProperties.prefix, "drugs", couchDbProperties.username!!, couchDbProperties.password!!, 1)
	}

	@Bean
	fun chapIVCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper, couchDbProperties: CouchDbLiteProperties): CouchDbDispatcher {
		return CouchDbDispatcherImpl(httpClient, objectMapper, couchDbProperties.prefix, "chapiv", couchDbProperties.username!!, couchDbProperties.password!!, 1)
	}

	@Bean
	fun systemCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper): CouchDbDispatcher {
		return CouchDbDispatcherImpl(httpClient, objectMapper, ICURE_PREFIX, "system", couchDbProperties.username!!, couchDbProperties.password!!, 1)
	}
}
