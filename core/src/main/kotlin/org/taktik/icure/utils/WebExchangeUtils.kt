package org.taktik.icure.utils

import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.reactor.ReactorContext
import org.springframework.web.server.ServerWebExchange

suspend fun addHeaderToExchange(header: String, data: () -> String) {
	coroutineContext[ReactorContext]?.context?.getOrEmpty<ServerWebExchange>(ServerWebExchange::class.java)?.orElse(null)
		?.let {
			it.response.headers.set(header, data())
		}
}
