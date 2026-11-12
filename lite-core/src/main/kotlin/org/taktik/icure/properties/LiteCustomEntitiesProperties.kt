package org.taktik.icure.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("icure.customentities")
class LiteCustomEntitiesProperties(
	val customEntitiesConfigurationPath: String? = null,
	val designDocSchemaPath: String? = null
)
