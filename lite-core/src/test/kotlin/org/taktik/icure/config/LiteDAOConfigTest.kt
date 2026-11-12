package org.taktik.icure.config

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.taktik.icure.properties.LiteDAOProperties

/**
 * [LiteDAOConfig] is mutable at runtime: `PUT /rest/v2/icure/lite/config/{property}/{value}` reaches
 * [LiteDAOConfig.setLiteConfig] through `ICureLiteLogicImpl.setKrakenLiteProperty`. Only two properties
 * may be changed that way, and an unknown name must be rejected rather than silently ignored.
 */
class LiteDAOConfigTest : StringSpec({

	"setLiteConfig should toggle useDataOwnerPartition" {
		val config = LiteDAOConfig(LiteDAOProperties())
		config.useDataOwnerPartition shouldBe false

		config.setLiteConfig("useDataOwnerPartition", true)
		config.useDataOwnerPartition shouldBe true
		// The other property must not be affected.
		config.useObsoleteViews shouldBe false

		config.setLiteConfig("useDataOwnerPartition", false)
		config.useDataOwnerPartition shouldBe false
	}

	"setLiteConfig should toggle useObsoleteViews" {
		val config = LiteDAOConfig(LiteDAOProperties())
		config.useObsoleteViews shouldBe false

		config.setLiteConfig("useObsoleteViews", true)
		config.useObsoleteViews shouldBe true
		config.useDataOwnerPartition shouldBe false
	}

	"setLiteConfig should reject an unrecognized property" {
		val config = LiteDAOConfig(LiteDAOProperties())
		listOf("", "UseObsoleteViews", "useobsoleteviews", "indexBuiltInViews", "unknown").forEach { property ->
			shouldThrow<IllegalArgumentException> {
				config.setLiteConfig(property, true)
			}.message shouldContain "is not a recognized property"
		}
	}

	"The query provider should stay in compatibility mode" {
		// In compatibility mode the QueryProvider does not fail when a view is missing from the schema,
		// which is what allows lite to run against a database where only some views were created.
		LiteDAOConfig(LiteDAOProperties()).queryProviderCompatibilityMode shouldBe true
	}

	"viewsToIndexAtStartup should be taken from the properties" {
		LiteDAOConfig(LiteDAOProperties()).viewsToIndexAtStartup shouldBe emptyList()
		LiteDAOConfig(LiteDAOProperties(viewsToIndexAtStartup = listOf("Code_Maurice", "Contact_DataOwner")))
			.viewsToIndexAtStartup shouldBe listOf("Code_Maurice", "Contact_DataOwner")
	}

	"Initial mutable flags should be taken from the properties" {
		with(LiteDAOConfig(LiteDAOProperties(useDataOwnerPartition = true, useObsoleteViews = true))) {
			useDataOwnerPartition shouldBe true
			useObsoleteViews shouldBe true
		}
	}

	"Indexation defaults should favour background indexation" {
		with(LiteDAOConfig(LiteDAOProperties())) {
			indexBuiltInViews shouldBe true
			forceForegroundIndexation shouldBe false
			backgroundIndexationWorkers shouldBe 1
		}
	}
})
