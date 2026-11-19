package org.taktik.icure.properties

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.taktik.icure.dao.writeSchemaFile
import kotlin.io.path.createTempFile
import kotlin.io.path.writeText

class LiteCustomEntitiesPropertiesTest : StringSpec({

	"Without a schema path the custom schema should be disabled and the default repository used" {
		with(LiteCustomEntitiesProperties()) {
			designDocSchema.shouldBeNull()
			customSchemaEnabled shouldBe false
			builtinViewsRepository shouldBe "https://github.com/icure/kraken-builtin-views"
		}
	}

	"The schema file should be loaded when the properties are created" {
		with(LiteCustomEntitiesProperties(designDocSchemaPath = writeSchemaFile(mapOf("Contact" to listOf("all", "conflicts"))))) {
			customSchemaEnabled shouldBe true
			designDocSchema?.viewsByEntity shouldBe mapOf("Contact" to mapOf("all" to 0, "conflicts" to 0))
		}
	}

	"A missing schema file should prevent the startup" {
		shouldThrow<IllegalStateException> {
			LiteCustomEntitiesProperties(designDocSchemaPath = "/does/not/exist.json")
		}.message shouldContain "is not a readable file"
	}

	"An invalid schema file should prevent the startup" {
		val path = createTempFile("invalid-schema", ".json").also {
			it.toFile().deleteOnExit()
			it.writeText("{ not json")
		}.toString()
		shouldThrow<IllegalStateException> {
			LiteCustomEntitiesProperties(designDocSchemaPath = path)
		}.message shouldContain "cannot parse the design doc schema"
	}
})
