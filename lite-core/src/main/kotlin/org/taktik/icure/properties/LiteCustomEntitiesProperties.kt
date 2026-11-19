package org.taktik.icure.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "icure.customentities")
class LiteCustomEntitiesProperties(
	val designDocSchemaPath: String? = null,
	val builtinViewsRepository: String? = null,
)