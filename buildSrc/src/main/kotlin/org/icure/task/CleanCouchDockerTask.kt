package org.icure.task

import com.icure.test.setup.ICureTestSetup
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CleanCouchDockerTask : DefaultTask() {

	@TaskAction
	fun cleanDocker() {
		val dockerName = dockerNameFile.readText()
		if (dockerName.isEmpty()) {
			println("Could not find any Docker to clean")
		} else {
			println("Stopping CouchDB Container $dockerName...")
			val stopped = ICureTestSetup.cleanContainer(dockerName)

			if (stopped) {
				println("CouchDB Container $dockerName properly destroyed")
			} else {
				println("ERROR: CouchDB Container $dockerName could not be properly destroyed: Destroy it manually")
			}
		}
	}
}
