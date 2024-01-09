package org.taktik.icure.cache.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Component
import org.taktik.icure.cache.ReactorCacheInjector
import reactor.core.publisher.Mono
import reactor.util.context.Context
import kotlin.coroutines.CoroutineContext

@Component
class LiteReactorCacheInjectorImpl : ReactorCacheInjector {
    override fun injectCacheInContext(ctx: Context, cacheSize: Int): CoroutineContext {
        require(cacheSize > 0)
        return ctx.asCoroutineContext()
    }

    override fun <T> monoWithCachedContext(cacheSize: Int, block: suspend CoroutineScope.() -> T?): Mono<T> {
        require(cacheSize > 0)
        return mono {
            block()
        }
    }
}