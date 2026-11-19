package org.taktik.icure.e2e

import java.io.File

/**
 * Writes a design doc schema file, in the format downloaded from the cockpit, for the scenario and returns its
 * absolute path, ready to be passed as `icure.customentities.designDocSchemaPath`.
 *
 * The partition indices are the cloud ones and are deliberately all 0: kraken-lite only reads the view names
 * from the file and allocates its own `_design/<Entity>-<n>` partitions.
 *
 * @param viewsByEntity entity simple name to the names of the views of that entity.
 */
fun writeDesignDocSchema(scenario: String, viewsByEntity: Map<String, List<String>>): String =
	File(E2eConfig.logDirectory.parentFile, "e2e-schemas/$scenario.json").also { file ->
		file.parentFile.mkdirs()
		file.writeText(
			objectMapper.writeValueAsString(
				mapOf(
					"_id" to "ddc:e2e-group:1",
					"viewsByEntity" to viewsByEntity.mapValues { (_, views) -> views.associateWith { 0 } },
				),
			),
		)
	}.absolutePath
