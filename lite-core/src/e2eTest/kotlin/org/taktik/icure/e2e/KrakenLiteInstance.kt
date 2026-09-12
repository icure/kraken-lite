package org.taktik.icure.e2e

import java.io.File
import java.net.ServerSocket
import java.util.concurrent.TimeUnit

/**
 * A kraken-lite instance running as a child JVM, started from the boot jar produced by `bootJar`.
 *
 * Each initialization scenario needs its own process: the behaviour under test happens once, at startup,
 * under a particular set of properties, so it can only be observed by booting again.
 */
class KrakenLiteInstance private constructor(
    private val process: Process,
    val port: Int,
    val logFile: File,
    val adminPassword: String?,
) : AutoCloseable {

    val baseUrl: String get() = "http://localhost:$port"

    /** An unauthenticated client; the permit-all endpoints are reachable with it. */
    fun client() = E2eHttpClient(baseUrl)

    /** A client authenticated as the bootstrapped admin user, through a JWT. */
    fun adminClient(): E2eHttpClient {
        val password = requireNotNull(adminPassword) {
            "The admin user was not created; boot with createAdminUser enabled to use adminClient()"
        }
        return client().withBearer(client().login(ADMIN_LOGIN, password))
    }

    fun logs(): String = logFile.readText()

    override fun close() {
        process.destroy()
        if (!process.waitFor(30, TimeUnit.SECONDS)) {
            process.destroyForcibly()
            process.waitFor(30, TimeUnit.SECONDS)
        }
    }

    companion object {
        const val ADMIN_LOGIN = "admin"

        private const val BOOT_TIMEOUT_SECONDS = 300L
        private val ADMIN_PASSWORD_PATTERN = Regex("Default admin user created with password (\\S+)")

        /**
         * Last line the startup runner writes. The health endpoint answers UP as soon as netty is listening,
         * which happens *before* the runner creates the admin user and logs its password, so waiting on
         * health alone is not enough to read the credentials back.
         */
        private const val STARTUP_COMPLETE_MARKER = ") is started"

        /**
         * Properties every scenario needs.
         *
         * `createAdminUser` is off by default in kraken-lite, and the user it creates has no
         * `healthcarePartyId`, so it only gets ROLE_USER. Since `LiteSecurityConfig` guards every
         * non permit-all path with ROLE_HCP unless `allowOnlyHcp` is false, the tests would be locked out of
         * every endpoint without both of these.
         *
         * The local code import is disabled because it parses the whole bundled XML corpus on startup, which
         * dominates the boot time and is irrelevant to what is under test.
         *
         * `objectstorage.cacheLocation` has no default and `LocalObjectStorageImpl.afterPropertiesSet`
         * rejects a blank one, so the context fails to start without it.
         */
        private fun defaultProperties(scenario: String) = mapOf(
            "icure.authentication.lite.createAdminUser" to "true",
            "icure.security.allowOnlyHcp" to "false",
            "icure.couchdb.populateDatabaseFromLocalXmls" to "false",
            "icure.couchdb.url" to E2eConfig.couchDbUrl,
            "icure.couchdb.username" to E2eConfig.couchDbUsername,
            "icure.couchdb.password" to E2eConfig.couchDbPassword,
            "icure.couchdb.prefix" to E2eConfig.DB_PREFIX,
            "icure.objectstorage.cacheLocation" to objectStorageCache(scenario).absolutePath,
            "icure.externalservices.useFakes" to "true",
        )

        /** A scratch directory per scenario, so instances never share their object storage cache. */
        private fun objectStorageCache(scenario: String): File =
            File(E2eConfig.logDirectory.parentFile, "e2e-storage/$scenario").also {
                it.deleteRecursively()
                it.mkdirs()
            }

        private fun bootJar(): File = E2eConfig.jarDirectory
            .listFiles { file -> file.name.endsWith(".jar") && !file.name.endsWith("-plain.jar") }
            ?.maxByOrNull { it.lastModified() }
            ?: error(
                "No boot jar found in ${E2eConfig.jarDirectory.absolutePath}. " +
                    "Run the tests through `./gradlew :lite-core:e2eTest`, which builds it first.",
            )

        private fun freePort(): Int = ServerSocket(0).use { it.localPort }

        /**
         * Boots an instance and waits until it reports itself healthy.
         *
         * @param scenario names the log file, so a failing run can be traced back to the scenario.
         * @param properties extra `-D` overrides, merged over (and able to override) the defaults.
         */
        fun start(
            scenario: String,
            properties: Map<String, String> = emptyMap(),
        ): KrakenLiteInstance {
            val port = freePort()
            val logFile = File(E2eConfig.logDirectory, "$scenario.log").also { it.delete() }
            val allProperties = defaultProperties(scenario) + properties + mapOf("server.port" to "$port")

            val command = buildList {
                add(System.getProperty("java.home") + "/bin/java")
                allProperties.forEach { (key, value) -> add("-D$key=$value") }
                add("-jar")
                add(bootJar().absolutePath)
                add("app")
            }

            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .redirectOutput(logFile)
                .start()

            return runCatching {
                awaitHealthy(process, port, logFile, scenario)
                KrakenLiteInstance(
                    process = process,
                    port = port,
                    logFile = logFile,
                    adminPassword = ADMIN_PASSWORD_PATTERN.find(logFile.readText())?.groupValues?.get(1),
                )
            }.getOrElse {
                process.destroyForcibly()
                throw it
            }
        }

        private fun awaitHealthy(process: Process, port: Int, logFile: File, scenario: String) {
            val client = E2eHttpClient("http://localhost:$port")
            val deadline = System.currentTimeMillis() + BOOT_TIMEOUT_SECONDS * 1000

            while (System.currentTimeMillis() < deadline) {
                check(process.isAlive) {
                    "kraken-lite died while starting scenario '$scenario'. Log:\n${logFile.readText().takeLast(8000)}"
                }
                val health = runCatching { client.get("/actuator/health") }.getOrNull()
                val healthy = health?.isSuccess() == true && health.json["status"]?.asText() == "UP"
                if (healthy && logFile.readText().contains(STARTUP_COMPLETE_MARKER)) return
                Thread.sleep(1000)
            }

            error(
                "kraken-lite did not become healthy within $BOOT_TIMEOUT_SECONDS seconds for scenario " +
                    "'$scenario'. Log:\n${logFile.readText().takeLast(8000)}",
            )
        }
    }
}

/**
 * Boots an instance for the duration of [block], destroying it afterwards even if the block fails.
 */
fun <T> withKrakenLite(
    scenario: String,
    properties: Map<String, String> = emptyMap(),
    block: (KrakenLiteInstance) -> T,
): T = KrakenLiteInstance.start(scenario, properties).use(block)
