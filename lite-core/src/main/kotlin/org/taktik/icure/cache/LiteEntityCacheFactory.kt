package org.taktik.icure.cache

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import org.taktik.icure.spring.asynccache.AsyncEntityCache

@Component
class LiteEntityCacheFactory(
    @Qualifier("asyncCacheManager") val asyncCacheManager: AsyncCacheManager
) : EntityCacheFactory {
    override fun <T : StoredDocument> localOnlyCache(entityClass: Class<T>) = null
    override fun <T : StoredDocument> localAndDistributedCache(entityClass: Class<T>) = AsyncEntityCache(entityClass, asyncCacheManager)
}
