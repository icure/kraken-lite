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

import org.slf4j.LoggerFactory
import org.taktik.icure.properties.IcureEntitiesCacheProperties
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class AsyncMapCacheManager(
	private val entitiesCacheProperties: IcureEntitiesCacheProperties
) : AsyncCacheManager {
	private val log = LoggerFactory.getLogger(AsyncMapCacheManager::class.java)

	private val caches: ConcurrentMap<String, Cache<*, *>> = ConcurrentHashMap()

	override fun <K : Serializable, V : Any> getCache(name: String): Cache<K, V> =
		caches.computeIfAbsent(
			name
		) { n -> CaffeineCacheWrapper<K, V>(n, getTtlSecondsForName(name)) } as Cache<K, V>

	/**
	 * Configures the time to leave given the map name, null means infinite
	 */
	private fun getTtlSecondsForName(name: String): Int? = when {
		name == "spring.security.tokens" -> 5 * 60
		name.startsWith("org.taktik.icure.entities.") -> entitiesCacheProperties.getConfigurationForName(name).ttl
		else -> null.also { log.warn("No TTL configured for cache $name - will be unlimited") }
	}
}
