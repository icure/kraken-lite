package org.taktik.icure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.taktik.icure.properties.IcureEntitiesCacheProperties
import org.taktik.icure.spring.asynccache.AsyncMapCacheManager

@Configuration
class LiteCoreConfig {
    @Bean
    fun asyncCacheManager(
        entitiesCacheProperties: IcureEntitiesCacheProperties
    ) = AsyncMapCacheManager(entitiesCacheProperties)
}
