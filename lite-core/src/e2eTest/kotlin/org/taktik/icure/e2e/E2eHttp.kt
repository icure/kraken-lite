package org.taktik.icure.e2e

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.Base64

val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule()

/**
 * The outcome of an http call: the tests assert on the status as much as on the body, so both are kept.
 */
data class HttpResult(val status: Int, val body: String) {
    val json: JsonNode by lazy { objectMapper.readTree(body.ifBlank { "{}" }) }

    fun isSuccess() = status in 200..299

    /** Fails with the body included, which is what makes a broken e2e run diagnosable. */
    fun orFail(what: String): HttpResult {
        check(isSuccess()) { "$what failed with HTTP $status: $body" }
        return this
    }
}

/**
 * A minimal JSON http client. Deliberately built on the JDK client rather than on a new dependency: the
 * root build uses `failOnVersionConflict()`, so every extra library on the test classpath is a liability.
 */
class E2eHttpClient(
    private val baseUrl: String,
    private val authorization: String? = null,
) {
    private val client: HttpClient = HttpClient.newBuilder()
        // Pinned to HTTP/1.1 on purpose: the JDK client otherwise attempts an h2c upgrade, and the server
        // answers such a request with 413 instead of processing it, which makes every POST fail.
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    fun withBasicAuth(username: String, password: String) = E2eHttpClient(
        baseUrl,
        "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray()),
    )

    fun withBearer(token: String) = E2eHttpClient(baseUrl, "Bearer $token")

    fun withoutAuth() = E2eHttpClient(baseUrl, null)

    fun get(path: String) = send("GET", path, null)

    fun post(path: String, body: Any? = null) = send("POST", path, body)

    fun put(path: String, body: Any? = null) = send("PUT", path, body)

    fun delete(path: String, body: Any? = null) = send("DELETE", path, body)

    private fun send(method: String, path: String, body: Any?): HttpResult {
        val payload = when (body) {
            null -> null
            is String -> body
            else -> objectMapper.writeValueAsString(body)
        }
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$baseUrl$path"))
            .timeout(Duration.ofSeconds(60))
            .header("Content-Type", "application/json")
            .apply { authorization?.let { header("Authorization", it) } }
            .method(
                method,
                payload?.let { HttpRequest.BodyPublishers.ofString(it) } ?: HttpRequest.BodyPublishers.noBody(),
            )
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return HttpResult(response.statusCode(), response.body())
    }
}

/**
 * Logs in through `POST /rest/v2/auth/login` and returns the JWT, which is how a real client authenticates
 * against kraken-lite.
 */
fun E2eHttpClient.login(username: String, password: String): String =
    post("/rest/v2/auth/login", mapOf("username" to username, "password" to password))
        .orFail("Login as $username")
        .json["token"].asText()
