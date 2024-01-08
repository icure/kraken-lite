package org.taktik.icure.services.external.http.websocket.operation

import org.springframework.web.reactive.socket.WebSocketSession

interface WebSocketOperationFactory {
	fun get(klass: Class<Operation>, webSocket: WebSocketSession): Operation
}
