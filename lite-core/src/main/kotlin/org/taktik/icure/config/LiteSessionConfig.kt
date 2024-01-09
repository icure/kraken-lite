/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.session.MapSession
import org.springframework.session.ReactiveMapSessionRepository
import org.springframework.session.ReactiveSessionRepository
import org.springframework.session.web.server.session.SpringSessionWebSessionStore
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.session.CookieWebSessionIdResolver
import org.springframework.web.server.session.DefaultWebSessionManager
import org.springframework.web.server.session.WebSessionIdResolver
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

@Configuration
@ConditionalOnProperty(prefix = "spring", name = ["session.enabled"], havingValue = "true", matchIfMissing = false)
class LiteSessionConfig {
	@Bean
	fun reactiveSessionRepository(): ReactiveSessionRepository<MapSession> {
		return ReactiveMapSessionRepository(ConcurrentHashMap(4096))
	}
}
