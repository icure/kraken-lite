package org.taktik.icure.customentities

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.icure.cardinal.customentities.config.VersionedCustomEntitiesConfiguration
import com.icure.cardinal.customentities.util.CachedCustomEntitiesConfigurationProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.taktik.icure.properties.LiteCustomEntitiesProperties
import java.io.File

@Component
class LiteCachedCustomEntitiesConfigurationProvider(
	properties: LiteCustomEntitiesProperties,
	@Qualifier("objectMapper") mapper: ObjectMapper
) : CachedCustomEntitiesConfigurationProvider {

	private val config: VersionedCustomEntitiesConfiguration? by lazy {
		properties.customEntitiesConfigurationPath?.let {
			mapper.readValue<VersionedCustomEntitiesConfiguration>(File(it).readText())
		}

	}

	override suspend fun getConfigForCurrentUser(): VersionedCustomEntitiesConfiguration? = config
}