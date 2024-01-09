package org.icure.task

import org.gradle.api.Task

val Task.dockerNameFile get() =
    project.layout.buildDirectory.asFile.get().resolve("couchdb_docker.txt")