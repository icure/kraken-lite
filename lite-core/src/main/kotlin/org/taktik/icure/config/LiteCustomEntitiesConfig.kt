package org.taktik.icure.config

import com.icure.cardinal.customentities.util.ExtendableBuiltinEntityValidatorMapperConfigsProviderImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LiteCustomEntitiesConfig {

	@Bean
	fun extendableBuiltinEntityValidatorMapperConfigsProvider() =
		ExtendableBuiltinEntityValidatorMapperConfigsProviderImpl(
			objectToDomain = emptyMap(),
			enumToDomain = emptyMap()
		)

}