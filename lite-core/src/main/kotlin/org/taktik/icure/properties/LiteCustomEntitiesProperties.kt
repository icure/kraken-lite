package org.taktik.icure.properties

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.boot.context.properties.ConfigurationProperties
import org.taktik.icure.entities.designdoc.DesignDocSchema
import java.io.File

@ConfigurationProperties(prefix = "icure.customentities")
class LiteCustomEntitiesProperties(
	val designDocSchemaPath: String? = null,
	val builtinViewsRepository: String = DEFAULT_BUILTIN_VIEWS_REPOSITORY,
) {

	/**
	 * The custom design doc schema loaded from [designDocSchemaPath], or null if no path was configured.
	 * The file is loaded when the properties are bound, so an invalid path or schema prevents the application startup.
	 */
	val designDocSchema: DesignDocSchema? = designDocSchemaPath?.let { path ->
		val file = File(path)
		check(file.isFile) { "Invalid config: icure.customentities.designDocSchemaPath ($path) is not a readable file" }
		try {
			jacksonObjectMapper().readValue<DesignDocSchema>(file)
		} catch (e: JacksonException) {
			throw IllegalStateException("Invalid config: cannot parse the design doc schema at $path", e)
		}
	}

	/**
	 * If true, only the system views are created from the code and the other views are created according to [designDocSchema].
	 */
	val customSchemaEnabled: Boolean get() = designDocSchema != null

	companion object {
		const val DEFAULT_BUILTIN_VIEWS_REPOSITORY = "https://github.com/icure/kraken-builtin-views"
	}
}
