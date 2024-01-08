package org.taktik.icure.services.external.http.websocket

import java.lang.reflect.Method
import org.taktik.icure.services.external.http.websocket.operation.Operation
import org.taktik.icure.services.external.http.websocket.operation.WebSocketOperationFactory

data class WebSocketInvocation(
    val operationClass: Class<Operation>,
    val factory: WebSocketOperationFactory,
    val bean: Any?,
    val method: Method
)
