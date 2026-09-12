package org.icure.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.net.HttpURLConnection
import java.net.URI
import java.util.Base64
import java.util.UUID

/**
 * Starts a single node CouchDB container for the end to end tests, mirroring what kraken-cloud does with
 * its own start task. A single node is required: [org.taktik.icure.asyncdao.impl.ICureLiteDAOImpl] refuses
 * to read or write the couchdb configuration when `_membership` reports more than one node, and the lite
 * startup runner does exactly that while setting `ken/batch_channels`.
 *
 * Nothing happens when the tests are pointed at a remote CouchDB, so CI can supply its own instance.
 */
open class StartCouchDockerTask : DefaultTask() {

    companion object {
        // Same image as compose/cardinal-deployment/docker-compose.yml.
        private const val COUCHDB_IMAGE = "couchdb:3.4.2"
        private const val STARTUP_TIMEOUT_SECONDS = 120L
    }

    @TaskAction
    fun startDocker() {
        // A previous run whose build failed before the e2e task started would have left its container
        // behind, since the clean task only runs as a finalizer of the tests. Remove it first so repeated
        // runs cannot pile up containers all bound to the same port.
        removeStaleContainer()

        val testProperties = readTestProperties(project.projectDir)

        val couchDbUrl = testProperties["icure.couchdb.url"]
            ?: throw IllegalStateException("Impossible to start a CouchDB Container: Missing icure.couchdb.url property")

        if (!couchDbUrl.contains("localhost") && !couchDbUrl.contains("127.0.0.1")) {
            logger.lifecycle("Not starting CouchDB Container: $couchDbUrl is not a local URL")
            dockerNameFile.writeText("")
            return
        }

        val username = testProperties["icure.couchdb.username"]
            ?: throw IllegalStateException("Impossible to start a CouchDB Container: Missing icure.couchdb.username property")
        val password = testProperties["icure.couchdb.password"]
            ?: throw IllegalStateException("Impossible to start a CouchDB Container: Missing icure.couchdb.password property")
        val port = URI(couchDbUrl).port.takeIf { it > 0 }
            ?: throw IllegalStateException("Impossible to start a CouchDB Container: no port in $couchDbUrl")

        val containerName = "couchdb-lite-test-${UUID.randomUUID().toString().take(8)}"
        logger.lifecycle("Starting CouchDB container $containerName on port $port...")

        runCommand(
            "docker", "run", "-d",
            "--name", containerName,
            "-p", "$port:5984",
            "-e", "COUCHDB_USER=$username",
            "-e", "COUCHDB_PASSWORD=$password",
            COUCHDB_IMAGE,
        )
        // Record the name immediately so the clean task can remove the container even if the setup below fails.
        dockerNameFile.writeText(containerName)

        awaitCouchDb(couchDbUrl, username, password)
        createSystemDatabases(couchDbUrl, username, password)

        logger.lifecycle("CouchDB container $containerName up and running")
    }

    private fun removeStaleContainer() {
        val stale = dockerNameFile.takeIf { it.exists() }?.readText()?.trim().orEmpty()
        if (stale.isNotEmpty()) {
            logger.lifecycle("Removing stale CouchDB container $stale left by a previous run")
            runCommandOrNull("docker", "rm", "-f", stale)
            dockerNameFile.writeText("")
        }
    }

    private fun awaitCouchDb(baseUrl: String, username: String, password: String) {
        val deadline = System.currentTimeMillis() + STARTUP_TIMEOUT_SECONDS * 1000
        while (System.currentTimeMillis() < deadline) {
            if (request("$baseUrl/_up", "GET", username, password) in 200..299) return
            Thread.sleep(500)
        }
        throw IllegalStateException("CouchDB did not become available within $STARTUP_TIMEOUT_SECONDS seconds")
    }

    /**
     * A bare container has none of the system databases; CouchDB reports itself as an unconfigured cluster
     * until they exist.
     */
    private fun createSystemDatabases(baseUrl: String, username: String, password: String) {
        listOf("_users", "_replicator", "_global_changes").forEach { database ->
            val status = request("$baseUrl/$database", "PUT", username, password)
            // 412 means the database is already there, which is fine.
            check(status in 200..299 || status == 412) {
                "Could not create the $database system database: HTTP $status"
            }
        }
    }

    private fun request(url: String, method: String, username: String, password: String): Int =
        runCatching {
            (URI(url).toURL().openConnection() as HttpURLConnection).run {
                requestMethod = method
                connectTimeout = 2000
                readTimeout = 5000
                setRequestProperty(
                    "Authorization",
                    "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray()),
                )
                try {
                    responseCode
                } finally {
                    disconnect()
                }
            }
        }.getOrDefault(-1)
}
