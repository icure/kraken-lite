package org.taktik.icure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.taktik.icure.config.CardinalVersionConfig.Companion.minCardinalModelVersion
import org.taktik.icure.entities.utils.SemanticVersion

@Profile("app")
@Configuration
class LiteCardinalVersionConfig : CardinalVersionConfig {

	@Value("\${icure.model.cardinalVersion:#{null}}")
	var cardinalVersion: String? = null

	override suspend fun getUserCardinalVersion(): SemanticVersion? = getConfiguredCardinalVersion()

	override suspend fun shouldUseCardinalModel(): Boolean = isConfiguredForCardinalModel()

	fun getConfiguredCardinalVersion(): SemanticVersion? = cardinalVersion?.let {
		SemanticVersion(it)
	}

	fun isConfiguredForCardinalModel(): Boolean =
		getConfiguredCardinalVersion()?.let { it >= minCardinalModelVersion } ?: false
}