package org.taktik.icure.services.external.http.websocket.factory

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketSession
import org.taktik.icure.services.external.http.websocket.operation.Operation
import org.taktik.icure.services.external.http.websocket.operation.WebSocketOperationFactory

@Component
class DefaultWebSocketOperationFactoryImpl(val objectMapper: ObjectMapper) : WebSocketOperationFactory {
	override fun get(klass: Class<Operation>, webSocket: WebSocketSession): Operation =
		klass.getConstructor(WebSocketSession::class.java, ObjectMapper::class.java).newInstance(webSocket, objectMapper)
}
