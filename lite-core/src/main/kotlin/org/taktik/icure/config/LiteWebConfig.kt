/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.services.external.http.WebSocketOperationLiteHandler
import org.taktik.icure.services.external.http.WsController
import org.taktik.icure.services.external.http.websocket.factory.DefaultWebSocketOperationFactoryImpl
import org.taktik.icure.services.external.http.websocket.operation.WebSocketOperationFactory

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
class LiteWebFluxConfigurer : SharedWebFluxConfiguration()