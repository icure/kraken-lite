/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.config

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.client.reactive.ReactorResourceFactory
import org.taktik.icure.asyncdao.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.EntityInfoDAOImpl
import org.taktik.icure.properties.CouchDbProperties
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

@Configuration
@Profile("app")
class CouchDbConfig(protected val couchDbProperties: CouchDbProperties) {
	val log: Log = LogFactory.getLog("org.taktik.icure.config.WebClient")

	@Bean
	fun connectionProvider(): ConnectionProvider {
		return ConnectionProvider.builder("LARGE_POOL")
			.maxConnections(50000)
			.maxIdleTime(Duration.ofSeconds(120))
			.pendingAcquireMaxCount(-1).build()
	}

	@Bean
	fun reactorClientResourceFactory(connectionProvider: ConnectionProvider) = ReactorResourceFactory().apply {
		isUseGlobalResources = false
		this.connectionProvider = connectionProvider
	}

	@Bean
	fun baseEntityInfoDao(
		@Qualifier("baseCouchDbDispatcher") baseCouchDbDispatcher: CouchDbDispatcher
	) = EntityInfoDAOImpl(baseCouchDbDispatcher)

	@Bean
	fun patientEntityInfoDao(
		@Qualifier("patientCouchDbDispatcher") patientCouchDbDispatcher: CouchDbDispatcher
	) = EntityInfoDAOImpl(patientCouchDbDispatcher)
}
