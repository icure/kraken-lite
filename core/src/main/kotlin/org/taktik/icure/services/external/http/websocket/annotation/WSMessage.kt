/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.http.websocket.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass
import org.springframework.web.reactive.socket.WebSocketMessage

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.FIELD)
@Retention(RetentionPolicy.RUNTIME)
annotation class WSMessage(val messageClass: KClass<*> = WebSocketMessage::class)
