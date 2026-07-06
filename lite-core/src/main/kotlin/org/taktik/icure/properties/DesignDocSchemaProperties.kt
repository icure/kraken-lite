/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("app")
@ConfigurationProperties("icure.designdoc.lite")
data class DesignDocSchemaProperties(
	var builtinViewsRepository: String? = null,
	var viewsByEntity: Map<String, List<String>> = emptyMap(),
)
