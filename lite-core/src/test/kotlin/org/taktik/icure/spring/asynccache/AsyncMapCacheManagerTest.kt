package org.taktik.icure.spring.asynccache

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.taktik.icure.entities.User
import org.taktik.icure.properties.IcureEntitiesCacheProperties

/**
 * In kraken-lite the distributed cache of kraken-cloud is replaced by this in-process Caffeine based
 * manager, so its TTL resolution and the wrapper semantics are lite-specific behaviour.
 */
class AsyncMapCacheManagerTest : StringSpec({

	val properties = IcureEntitiesCacheProperties()

	"getCache should return the same instance for the same name" {
		val manager = AsyncMapCacheManager(properties)
		val first = manager.getCache<String, String>("org.taktik.icure.entities.User")
		val second = manager.getCache<String, String>("org.taktik.icure.entities.User")

		first shouldBe second
		manager.getCache<String, String>("spring.security.tokens") shouldNotBe first
	}

	"Caches should be named after the key they were requested with" {
		val manager = AsyncMapCacheManager(properties)
		manager.getCache<String, String>("spring.security.tokens").getName() shouldBe "spring.security.tokens"
	}

	"The security token cache should expire, entity caches should use the configured ttl" {
		val manager = AsyncMapCacheManager(properties)

		ttlOf(manager.getCache<String, String>("spring.security.tokens")) shouldBe 5 * 60
		ttlOf(manager.getCache<String, String>(User::class.java.name)) shouldBe
			properties.getConfigurationForName(User::class.java.name).ttl
		// Anything else is unlimited - the manager logs a warning for these.
		ttlOf(manager.getCache<String, String>("some.unconfigured.cache")).shouldBeNull()
	}

	"An unknown entity cache name should be rejected by the properties" {
		val manager = AsyncMapCacheManager(properties)
		runCatching {
			manager.getCache<String, String>("org.taktik.icure.entities.NotAnEntity")
		}.isFailure shouldBe true
	}

	"CaffeineCacheWrapper should store, evict and clear entries" {
		val cache = CaffeineCacheWrapper<String, String>("test", null)

		cache.get("absent").shouldBeNull()

		cache.put("a", "1")
		cache.put("b", "2")
		cache.get("a") shouldBe "1"
		cache.iterator().asSequence().map { it.key }.toSet() shouldBe setOf("a", "b")

		cache.evict("a")
		cache.get("a").shouldBeNull()
		cache.get("b") shouldBe "2"

		cache.clear()
		cache.get("b").shouldBeNull()
	}

	"CaffeineCacheWrapper invalidate should clear the cache and report false" {
		val cache = CaffeineCacheWrapper<String, String>("test", null)
		cache.put("a", "1")

		// `invalidate` returns false: there is nothing to propagate to other nodes in a lite installation.
		cache.invalidate() shouldBe false
		cache.get("a").shouldBeNull()
	}
})

/**
 * Reads back the TTL Caffeine was configured with, which is the only externally observable difference
 * between the caches handed out by [AsyncMapCacheManager].
 */
private fun ttlOf(cache: Cache<*, *>): Int? =
	(cache as CaffeineCacheWrapper<*, *>).cache.policy().expireAfterWrite().orElse(null)
		?.expiresAfter?.seconds?.toInt()
