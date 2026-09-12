package org.icure.task

import org.gradle.api.Task
import java.io.File

val Task.dockerNameFile get() =
    project.layout.buildDirectory.asFile.get().also {
        if (!it.exists()) it.mkdirs()
    }.resolve("couchdb_docker.txt")

/**
 * Runs a command and returns its trimmed stdout, throwing if it does not succeed.
 */
internal fun runCommand(vararg command: String): String {
    val process = ProcessBuilder(*command)
        .redirectErrorStream(true)
        .start()
    val output = process.inputStream.bufferedReader().readText().trim()
    val exitCode = process.waitFor()
    check(exitCode == 0) { "Command `${command.joinToString(" ")}` failed with exit code $exitCode: $output" }
    return output
}

/**
 * Runs a command, returning null instead of throwing when it fails.
 */
internal fun runCommandOrNull(vararg command: String): String? = runCatching { runCommand(*command) }.getOrNull()

/**
 * Reads the couchdb coordinates the tests expect from the test properties file, using the same convention
 * as kraken-cloud: the container is only started when the tests point at a local CouchDB.
 */
internal fun readTestProperties(projectDir: File): Map<String, String> =
    File(projectDir, "src/test/resources/icure-test.properties")
        .readLines()
        .filter { it.contains('=') && !it.trimStart().startsWith('#') }
        .associate { line -> line.substringBefore('=').trim() to line.substringAfter('=').trim() }
