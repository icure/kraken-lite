/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.http.websocket.operation

import java.io.IOException
import java.util.UUID
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.apache.commons.logging.LogFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.web.reactive.socket.WebSocketSession
import org.taktik.icure.services.external.http.websocket.AsyncProgress
import org.taktik.icure.services.external.http.websocket.Message
import reactor.core.publisher.Mono

abstract class BinaryOperation(protected var webSocket: WebSocketSession, protected var objectMapper: ObjectMapper) : Operation,
	AsyncProgress {
	private val log = LogFactory.getLog(BinaryOperation::class.java)

	@Throws(IOException::class)
	suspend fun binaryResponse(response: Flow<DataBuffer>) {
		val buffers = response.toList()
		webSocket.send(Mono.just(webSocket.binaryMessage { dbf -> dbf.join(buffers.map { dbf.wrap(it.asByteBuffer()) }) })).awaitFirstOrNull()
	}

	@Throws(IOException::class)
	suspend fun errorResponse(e: Exception) {
		val ed: MutableMap<String, String?> = HashMap()
		ed["message"] = e.message
		ed["localizedMessage"] = e.localizedMessage
		log.info("Error in socket " + e.message + ":" + e.localizedMessage + " ", e)
		webSocket.send(Mono.just(webSocket.textMessage(objectMapper.writeValueAsString(ed)))).awaitFirstOrNull()
	}

	@Throws(IOException::class)
	override suspend fun progress(progress: Double) {
		val wrapper = HashMap<String, Double>()
		wrapper["progress"] = progress
		val message: Message<*> = Message("progress", "Map", UUID.randomUUID().toString(), listOf(wrapper))
		webSocket.send(Mono.just(webSocket.textMessage(objectMapper.writeValueAsString(message)))).awaitFirstOrNull()
	}
}
