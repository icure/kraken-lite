/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.http.websocket.operation

interface Operation {
	fun handle(message: String)
	fun error(e: Throwable) {}
	fun complete() {}
}
