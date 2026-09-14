package org.icure.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Destroys the container started by [StartCouchDockerTask]. Wired as `finalizedBy` on the e2e test task so
 * that a failing test run still cleans up after itself.
 */
open class CleanCouchDockerTask : DefaultTask() {

    @TaskAction
    fun cleanDocker() {
        val dockerName = dockerNameFile.takeIf { it.exists() }?.readText()?.trim().orEmpty()
        if (dockerName.isEmpty()) {
            logger.lifecycle("No CouchDB container to clean")
            return
        }

        logger.lifecycle("Stopping CouchDB container $dockerName...")
        if (runCommandOrNull("docker", "rm", "-f", dockerName) != null) {
            logger.lifecycle("CouchDB container $dockerName properly destroyed")
            dockerNameFile.writeText("")
        } else {
            logger.error("ERROR: CouchDB container $dockerName could not be destroyed: remove it manually")
        }
    }
}
