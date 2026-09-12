package org.taktik.icure.e2e

import com.fasterxml.jackson.databind.JsonNode
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Direct access to the CouchDB behind the tested instance. The initialization scenarios assert on what the
 * database actually contains, which is the only way to tell "the design documents were created" apart from
 * "the application started without complaining".
 */
object CouchDbAdmin {

    private val client = E2eHttpClient(E2eConfig.couchDbUrl)
        .withBasicAuth(E2eConfig.couchDbUsername, E2eConfig.couchDbPassword)

    fun allDatabases(): List<String> =
        client.get("/_all_dbs").orFail("Listing databases").json.map { it.asText() }

    fun databaseExists(name: String): Boolean = client.get("/$name").isSuccess()

    /**
     * Deletes every database created by a tested instance, giving the next scenario a genuinely new
     * database. The system databases (`_users` and friends) are left alone: they belong to the container.
     */
    fun wipeIcureDatabases() {
        allDatabases()
            .filter { it.startsWith("${E2eConfig.DB_PREFIX}-") }
            .forEach { client.delete("/$it").orFail("Deleting database $it") }
    }

    fun designDocumentIds(database: String): List<String> =
        client.get("/$database/_all_docs?startkey=%22_design/%22&endkey=%22_design0%22")
            .orFail("Listing design documents of $database")
            .json["rows"].map { it["id"].asText() }

    fun designDocument(database: String, id: String): JsonNode? =
        client.get("/$database/$id").takeIf { it.isSuccess() }?.json

    fun createDesignDocument(database: String, id: String, body: Map<String, Any>) {
        client.put("/$database/$id", body).orFail("Creating design document $id in $database")
    }

    fun documentCount(database: String, startKey: String, endKey: String): Int =
        // Keys have to be encoded: code ids contain `|`, which is not legal in a url query.
        client.get("/$database/_all_docs?startkey=${encodeKey(startKey)}&endkey=${encodeKey(endKey)}")
            .orFail("Counting documents of $database")
            .json["rows"].size()

    private fun encodeKey(key: String): String =
        URLEncoder.encode("\"$key\"", StandardCharsets.UTF_8)

    /** Number of nodes in the cluster: kraken-lite refuses to run against more than one. */
    fun nodeCount(): Int =
        client.get("/_membership").orFail("Reading membership").json["all_nodes"].size()
}

/**
 * Polls [condition] until it holds or [timeoutSeconds] elapses.
 *
 * Needed because a good part of the startup work - the Maurice and DataOwner partitions, and the whole
 * custom design document schema - is launched on `GlobalScope` by
 * `ICureBackendApplication.createPartitionedDesignDocAndWarmupIfNeeded` and therefore keeps running well
 * after the application reports itself started.
 */
fun waitUntil(what: String, timeoutSeconds: Long = 180, condition: () -> Boolean) {
    val deadline = System.currentTimeMillis() + timeoutSeconds * 1000
    while (System.currentTimeMillis() < deadline) {
        if (runCatching(condition).getOrDefault(false)) return
        Thread.sleep(1000)
    }
    error("Timed out after ${timeoutSeconds}s waiting for: $what")
}
