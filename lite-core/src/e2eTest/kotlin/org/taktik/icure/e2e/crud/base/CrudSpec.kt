package org.taktik.icure.e2e.crud.base

import org.taktik.icure.e2e.E2eHttpClient
import org.taktik.icure.e2e.HttpResult
import org.taktik.icure.e2e.withDelegationFor
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Describes one entity's CRUD surface, so each entity in the matrix is a short declaration rather than yet
 * another copy of the same five requests. Same idea as the `crud/base` helpers of cloud-core, scaled down to
 * what kraken-lite actually exposes.
 */
data class CrudSpec(
    /** Human-readable name, used in the test names. */
    val name: String,
    /** Controller base path, for example `/rest/v2/hcparty`. */
    val path: String,
    /** The CouchDB database the entity is expected to be stored in. */
    val database: String,
    /** Builds the creation payload for the given entity id. */
    val create: (id: String) -> Map<String, Any?>,
    /** The field the modification step changes. */
    val modifiedField: String,
    /** The value that field is set to. */
    val modifiedValue: String,
    /** Listing endpoint relative to [path]; null when the entity exposes no simple listing. */
    val listSuffix: String? = "",
    /** True when the entity carries encryption metadata and therefore needs a delegation. */
    val encrypted: Boolean = false,
    /**
     * Overrides the default `DELETE {path}/{id}`. Some entities require the revision as a query parameter,
     * and MedicalLocation exposes no single delete at all.
     */
    val delete: (client: E2eHttpClient, id: String, rev: String) -> HttpResult = { client, id, _ ->
        client.delete("$path/${id.encoded()}")
    },
) {
    /** The creation payload, carrying a fake delegation when the entity needs one. */
    fun createPayload(id: String, dataOwnerId: String): Map<String, Any?> =
        create(id).let { if (encrypted) it.withDelegationFor(dataOwnerId) else it }

    fun getPath(id: String) = "$path/${id.encoded()}"

    companion object {
        /** Code ids contain `|`, which has to survive the round trip through the url. */
        fun String.encoded(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)
    }
}

private fun String.encoded(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)
