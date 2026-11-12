package org.taktik.icure.properties

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.boot.context.properties.source.ConfigurationPropertySources
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources

/**
 * Binds [LiteDAOProperties] the way Spring Boot does for a constructor-bound `@ConfigurationProperties` class
 * (constructor binding, registered through `@EnableConfigurationProperties`), using camelCase keys like `-Dicure.dao.xxx=...`.
 */
class LiteDAOPropertiesTest : StringSpec({

	"all icure.dao properties should bind from camelCase keys" {
		val sources = MutablePropertySources().apply {
			addFirst(
				MapPropertySource(
					"systemProperties",
					mapOf(
						"icure.dao.useDataOwnerPartition" to "true",
						"icure.dao.useObsoleteViews" to "true",
						"icure.dao.forceForegroundIndexation" to "true",
						"icure.dao.viewsToIndexAtStartup" to "Code_Maurice,Contact_DataOwner",
						"icure.dao.backgroundIndexationWorkers" to "8",
						"icure.dao.indexBuiltInViews" to "false",
					),
				),
			)
		}
		val bound = Binder(ConfigurationPropertySources.from(sources))
			.bind("icure.dao", Bindable.of(LiteDAOProperties::class.java))
			.get()

		bound.useDataOwnerPartition shouldBe true
		bound.useObsoleteViews shouldBe true
		bound.forceForegroundIndexation shouldBe true
		bound.viewsToIndexAtStartup shouldBe listOf("Code_Maurice", "Contact_DataOwner")
		bound.backgroundIndexationWorkers shouldBe 8
		bound.indexBuiltInViews shouldBe false
	}
})
