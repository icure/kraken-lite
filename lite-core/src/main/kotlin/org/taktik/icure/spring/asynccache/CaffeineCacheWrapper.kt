/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.spring.asynccache

import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit

class CaffeineCacheWrapper<K : Any, V: Any>(
	private val name: String,
	ttlSeconds: Int?
) : Cache<K, V> {
	val  cache: com.github.benmanes.caffeine.cache.Cache<K, V> = Caffeine.newBuilder()
		.maximumSize(10_000)
		.apply { ttlSeconds?.let { expireAfterWrite(it.toLong(), TimeUnit.SECONDS) } }
		.build()

	override suspend fun get(key: K): V? = cache.getIfPresent(key)

	override fun clear() {
		cache.invalidateAll()
	}

	override fun invalidate(): Boolean {
		clear()
		return false
	}

	override suspend fun evict(key: K) {
		cache.invalidate(key)
	}

	override suspend fun put(key: K, value: V) {
		cache.put(key, value)
	}

	override fun getName(): String {
		return name
	}

	override fun iterator(): Iterator<Map.Entry<K, V>> {
		return cache.asMap().toMap().iterator()
	}
}
