/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.asynccache

import org.springframework.cache.Cache
import java.util.concurrent.Callable

class NoopCache : Cache {
    override fun clear() {}
    override fun evict(key: Any) {}
    override fun get(key: Any): Cache.ValueWrapper? {
        return null
    }

    override fun <T> get(key: Any, type: Class<T>?): T? {
        return null
    }

    override fun <T> get(key: Any, valueLoader: Callable<T>): T? {
        return null
    }

    override fun getName(): String {
        return "noop"
    }

    override fun getNativeCache(): Any {
        return Any()
    }

    override fun put(key: Any, value: Any?) {}
    override fun putIfAbsent(key: Any, value: Any?): Cache.ValueWrapper? {
        return null
    }
}
