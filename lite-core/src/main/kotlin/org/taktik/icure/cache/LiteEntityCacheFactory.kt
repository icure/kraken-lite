package org.taktik.icure.cache

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import org.taktik.icure.spring.asynccache.AsyncEntityCache
import java.io.Serializable

@Component
class LiteEntityCacheFactory(
    @param:Qualifier("asyncCacheManager") val asyncCacheManager: AsyncCacheManager
) : EntityCacheFactory {

    override fun <K : Serializable, T : StoredDocument> localOnlyCache(
        entityClass: Class<T>
    ): EntityCacheChainLink<K, T>? = null

    override fun <K : Serializable, T : StoredDocument> localAndDistributedCache(
        entityClass: Class<T>
    ): EntityCacheChainLink<K, T>? = AsyncEntityCache(entityClass, asyncCacheManager)
}
