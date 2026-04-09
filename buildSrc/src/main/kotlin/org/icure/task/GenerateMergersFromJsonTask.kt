package org.icure.task

import com.icure.codegen.generator.MergerGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GenerateMergersFromJsonTask : DefaultTask() {

	@get:InputDirectory
	abstract val inputFolder: DirectoryProperty

	@get:OutputDirectory
	abstract val outputFolder: DirectoryProperty

	@get:InputDirectory
	@get:Optional
	abstract val additionalInputFolder: DirectoryProperty

	@TaskAction
	fun generateMergers() {
		if (!inputFolder.isPresent || !inputFolder.get().asFile.exists()) {
			logger.warn("Skipping generateMergersFromJson: input directory does not exist.")
			throw IllegalStateException("Input folder does not exist.")
		}
		val inputDir = inputFolder.get().asFile
		val outputDir = outputFolder.get().asFile

		outputDir.mkdirs()
		MergerGenerator(
			jsonFolder = inputDir.absolutePath,
			generationFolder = outputDir.absolutePath,
			*listOfNotNull(
				if (additionalInputFolder.isPresent) additionalInputFolder.get().asFile.absolutePath else null
			).toTypedArray()
		).generate()
	}
}
