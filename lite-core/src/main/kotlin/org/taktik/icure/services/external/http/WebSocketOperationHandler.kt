/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
@file:Suppress("BlockingMethodInNonBlockingContext")

package org.taktik.icure.services.external.http

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.server.PathContainer
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.web.reactive.socket.CloseStatus
import org.springframework.web.reactive.socket.WebSocketSession
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.services.external.http.websocket.annotation.WSMessage
import org.taktik.icure.services.external.http.websocket.annotation.WSParam
import org.taktik.icure.services.external.http.websocket.factory.DefaultWebSocketOperationFactoryImpl
import org.taktik.icure.services.external.http.websocket.operation.WebSocketOperationFactory
import reactor.core.publisher.Mono
import java.net.URI

class WebSocketOperationLiteHandler(
    wsControllers: List<WsController>,
    objectMapper: ObjectMapper,
    sessionInformationProvider: SessionInformationProvider,
    operationFactories: List<WebSocketOperationFactory>,
    defaultFactory: DefaultWebSocketOperationFactoryImpl,
) : WebSocketOperationHandler(
    wsControllers,
    objectMapper,
    sessionInformationProvider,
    operationFactories,
    defaultFactory
) {

    override fun handle(session: WebSocketSession): Mono<Void> = mono {
        val path = session.handshakeInfo.uri.path.replaceFirst(
            "^",
            ""
        ).replaceFirst(";.+?=.*".toRegex(), "")
        val pathContainer = PathContainer.parsePath(session.handshakeInfo.uri.path)
        val pathMethod = methods[path] ?: methods.entries.find { (_, p) ->
            p.first.matches(pathContainer)
        }?.value

        var operation = pathMethod?.let { pm ->
            val invocation = pm.second
            if (invocation.method.parameters.any {
                    it.getAnnotation(WSParam::class.java) != null || it.getAnnotation(
                        WSMessage::class.java
                    ) != null
                }) null
            else invocation.factory.get(invocation.operationClass, session).also {
                launchOperation(it, pathMethod, pathContainer).awaitFirstOrNull()
            }
        }

        pathMethod?.let { pm ->
            session.receive().doOnNext { wsm ->
                wsm.retain()
            }.asFlow().collect { wsm ->
                try {
                    val payloadAsText = wsm.payloadAsText

                    if (operation == null) {
                        val invocation = pm.second
                        invocation.factory.get(invocation.operationClass, session).also { operation = it }.let {
                            launch {
                                try {
                                    launchOperation(it, pathMethod, pathContainer, wsm).awaitFirstOrNull()
                                } catch (e: Exception) {
                                    handleOperationError(session, e)
                                } finally {
                                    wsm.release()
                                }
                            }
                        }
                    } else {
                        try {
                            //wsm.payloadAsText works for binary or text messages
                            operation!!.handle(payloadAsText)
                        } catch (e: Exception) {
                            handleOperationError(session, e)
                        } finally {
                            wsm.release()
                        }
                    }
                } catch (e: Exception) {
                    handleOperationError(session, e)
                }
            }
            operation?.complete()
        } ?: throw IllegalArgumentException("No operation found for path $path")

        null
    }
}
