package org.taktik.icure.config

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * [LiteDAOConfig] is mutable at runtime: `PUT /rest/v2/icure/lite/config/{property}/{value}` reaches
 * [LiteDAOConfig.setLiteConfig] through `ICureLiteLogicImpl.setKrakenLiteProperty`. Only two properties
 * may be changed that way, and an unknown name must be rejected rather than silently ignored.
 */
class LiteDAOConfigTest : StringSpec({

	"setLiteConfig should toggle useDataOwnerPartition" {
		val config = LiteDAOConfig()
		config.useDataOwnerPartition shouldBe false

		config.setLiteConfig("useDataOwnerPartition", true)
		config.useDataOwnerPartition shouldBe true
		// The other property must not be affected.
		config.useObsoleteViews shouldBe false

		config.setLiteConfig("useDataOwnerPartition", false)
		config.useDataOwnerPartition shouldBe false
	}

	"setLiteConfig should toggle useObsoleteViews" {
		val config = LiteDAOConfig()
		config.useObsoleteViews shouldBe false

		config.setLiteConfig("useObsoleteViews", true)
		config.useObsoleteViews shouldBe true
		config.useDataOwnerPartition shouldBe false
	}

	"setLiteConfig should reject an unrecognized property" {
		val config = LiteDAOConfig()
		listOf("", "UseObsoleteViews", "useobsoleteviews", "indexBuiltInViews", "unknown").forEach { property ->
			shouldThrow<IllegalArgumentException> {
				config.setLiteConfig(property, true)
			}.message shouldContain "is not a recognized property"
		}
	}

	"The query provider should stay in compatibility mode" {
		// In compatibility mode the QueryProvider does not fail when a view is missing from the schema,
		// which is what allows lite to run against a database where only some views were created.
		LiteDAOConfig().queryProviderCompatibilityMode shouldBe true
	}

	"viewsToIndexAtStartup should split the comma separated property" {
		val config = LiteDAOConfig()

		// Default: the empty property splits into a single empty string rather than an empty list. This is
		// harmless because the list is only ever consulted with `contains`, but pin it so that a future
		// change to the parsing is a deliberate one.
		config.viewsToIndexAtStartup shouldBe listOf("")
		config.viewsToIndexAtStartup.contains("Code_Maurice") shouldBe false

		setCommaSeparatedViews(config, "Code_Maurice,Contact_DataOwner")
		config.viewsToIndexAtStartup shouldBe listOf("Code_Maurice", "Contact_DataOwner")
	}

	"Indexation defaults should favour background indexation" {
		with(LiteDAOConfig()) {
			indexBuiltInViews shouldBe true
			forceForegroundIndexation shouldBe false
			backgroundIndexationWorkers shouldBe 1
		}
	}
})

/**
 * `commaSeparatedViewsToIndexAtStartup` is private and populated by Spring through `@Value`, so it is set
 * reflectively here to exercise the parsing performed by [LiteDAOConfig.viewsToIndexAtStartup].
 */
private fun setCommaSeparatedViews(config: LiteDAOConfig, value: String) {
	LiteDAOConfig::class.java.getDeclaredField("commaSeparatedViewsToIndexAtStartup").apply {
		isAccessible = true
		set(config, value)
	}
}
