package org.taktik.icure.e2e

import java.io.File

/**
 * Coordinates of the CouchDB started by the `startCouchDb` Gradle task, read from the same properties file
 * that task reads so the two can never drift apart.
 */
object E2eConfig {

    private val properties: Map<String, String> by lazy {
        val stream = E2eConfig::class.java.classLoader.getResourceAsStream("icure-test.properties")
            ?: error("icure-test.properties is not on the test classpath")
        stream.bufferedReader().readLines()
            .filter { it.contains('=') && !it.trimStart().startsWith('#') }
            .associate { it.substringBefore('=').trim() to it.substringAfter('=').trim() }
    }

    val couchDbUrl: String get() = properties.getValue("icure.couchdb.url")
    val couchDbUsername: String get() = properties.getValue("icure.couchdb.username")
    val couchDbPassword: String get() = properties.getValue("icure.couchdb.password")

    /** Prefix used for the databases created by the tested instance. */
    const val DB_PREFIX = "icure"

    /** The databases the four dispatchers of `CouchDbLiteConfig` are expected to open. */
    val expectedDatabases = listOf("$DB_PREFIX-base", "$DB_PREFIX-patient", "$DB_PREFIX-healthdata", "$DB_PREFIX-system")

    /**
     * Directory holding the boot jar. Set by the `e2eTest` Gradle task; falls back to the conventional
     * location so the suite can also be run from an IDE.
     */
    val jarDirectory: File
        get() = File(System.getProperty("icure.e2e.jarDir") ?: "lite-core/build/libs")

    /** Directory where the stdout/stderr of each launched instance is captured. */
    val logDirectory: File
        get() = File(System.getProperty("icure.e2e.logDir") ?: "lite-core/build/e2e-logs")
            .also { it.mkdirs() }
}
