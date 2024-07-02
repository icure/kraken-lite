/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.util.MimeType
import org.springframework.web.reactive.config.EnableWebFlux
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.services.external.http.WebSocketOperationLiteHandler
import org.taktik.icure.services.external.http.WsController
import org.taktik.icure.services.external.http.websocket.factory.DefaultWebSocketOperationFactoryImpl
import org.taktik.icure.services.external.http.websocket.operation.WebSocketOperationFactory
import org.taktik.icure.spring.encoder.PaginatedCollectingJackson2JsonEncoder

@Configuration
class LiteWebConfig {
	@Bean
	fun webSocketHandler(
		wsControllers: List<WsController>,
		sessionInformationProvider: SessionInformationProvider,
		objectMapper: ObjectMapper,
		operationFactories: List<WebSocketOperationFactory>,
		defaultFactory: DefaultWebSocketOperationFactoryImpl
	) = WebSocketOperationLiteHandler(wsControllers, objectMapper, sessionInformationProvider, operationFactories, defaultFactory)
}

@Configuration
@EnableWebFlux
class LiteWebFluxConfigurer(
	private val pluginsManager: PluginsManager
) : SharedWebFluxConfiguration() {

	override fun getJackson2JsonEncoder(): Jackson2JsonEncoder {
		val objectMapper = ObjectMapper().registerModule(
			KotlinModule.Builder()
				.configure(KotlinFeature.NullIsSameAsDefault, true)
				.build()
		).apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }
		return try {
			pluginsManager.newInstance<Jackson2JsonEncoder>(
				"org.taktik.icure.spring.encoder.PaginatedJackson2JsonEncoder",
				objectMapper, arrayOf<MimeType>()
			)
		} catch (e: PluginsManager.InvalidPluginException) {
			PaginatedCollectingJackson2JsonEncoder(objectMapper)
		}
	}


}