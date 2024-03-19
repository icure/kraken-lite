package org.taktik.icure.cache.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Component
import org.taktik.icure.cache.ReactorCacheInjector
import reactor.core.publisher.Mono
import kotlin.coroutines.CoroutineContext

@Component
class LiteReactorCacheInjectorImpl : ReactorCacheInjector {
    override fun injectCacheInContext(ctx: ReactorContext, cacheSize: Int): CoroutineContext {
        require(cacheSize > 0)
        return ctx
    }

    override fun <T> monoWithCachedContext(cacheSize: Int, block: suspend CoroutineScope.() -> T?): Mono<T> {
        require(cacheSize > 0)
        return mono {
            block()
        }
    }
}