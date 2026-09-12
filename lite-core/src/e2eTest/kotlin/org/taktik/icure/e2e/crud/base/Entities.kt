package org.taktik.icure.e2e.crud.base

import org.taktik.icure.e2e.E2eConfig

/**
 * The entity matrix covered by the CRUD end to end suite.
 *
 * The encrypted ones matter most for kraken-lite: `CouchDbLiteConfig` opens one dispatcher per database
 * family, and Patient / Contact / HealthElement / Document / Form are the only entities that exercise the
 * `icure-patient` and `icure-healthdata` dispatchers at all.
 */
object Entities {

    private const val BASE = "${E2eConfig.DB_PREFIX}-base"
    private const val PATIENT = "${E2eConfig.DB_PREFIX}-patient"
    private const val HEALTHDATA = "${E2eConfig.DB_PREFIX}-healthdata"

    val healthcareParty = CrudSpec(
        name = "HealthcareParty",
        path = "/rest/v2/hcparty",
        database = BASE,
        create = { id -> mapOf("id" to id, "firstName" to "Ada", "lastName" to "Lovelace") },
        modifiedField = "lastName",
        modifiedValue = "Byron",
        listSuffix = "?limit=100",
    )

    val user = CrudSpec(
        name = "User",
        path = "/rest/v2/user",
        database = BASE,
        create = { id -> mapOf("id" to id, "login" to "login-$id", "email" to "$id@example.com") },
        modifiedField = "email",
        modifiedValue = "changed@example.com",
        listSuffix = "?limit=100",
    )

    val code = CrudSpec(
        name = "Code",
        path = "/rest/v2/code",
        database = BASE,
        // Code ids are structured as type|code|version rather than being free form.
        create = { id -> mapOf("id" to id, "type" to "e2e", "code" to id.substringAfter('|').substringBefore('|'), "version" to "1", "label" to mapOf("en" to "Label"), "author" to "initial") },
        // type/code/version make up the id, so `author` is the only freely modifiable simple field.
        modifiedField = "author",
        modifiedValue = "modified",
        listSuffix = null,
        // Unlike the other controllers, deleting a code requires the revision as a query parameter.
        delete = { client, id, rev -> client.delete("/rest/v2/code/${java.net.URLEncoder.encode(id, Charsets.UTF_8)}?rev=$rev") },
    )

    val insurance = CrudSpec(
        name = "Insurance",
        path = "/rest/v2/insurance",
        database = BASE,
        create = { id -> mapOf("id" to id, "name" to mapOf("en" to "Insurer"), "code" to "123") },
        modifiedField = "code",
        modifiedValue = "456",
        listSuffix = null,
    )

    val medicalLocation = CrudSpec(
        name = "MedicalLocation",
        path = "/rest/v2/medicallocation",
        database = BASE,
        create = { id -> mapOf("id" to id, "name" to "Clinic") },
        modifiedField = "name",
        modifiedValue = "Renamed Clinic",
        listSuffix = "",
        // MedicalLocation is the one entity with no `DELETE /{id}`; it only exposes the batch endpoint.
        delete = { client, id, _ -> client.post("/rest/v2/medicallocation/delete/batch", mapOf("ids" to listOf(id))) },
    )

    val patient = CrudSpec(
        name = "Patient",
        path = "/rest/v2/patient",
        database = PATIENT,
        create = { id -> mapOf("id" to id, "firstName" to "Grace", "lastName" to "Hopper", "note" to "a note") },
        modifiedField = "lastName",
        modifiedValue = "Murray",
        listSuffix = null,
        encrypted = true,
    )

    val contact = CrudSpec(
        name = "Contact",
        path = "/rest/v2/contact",
        database = HEALTHDATA,
        create = { id -> mapOf("id" to id, "descr" to "a contact") },
        modifiedField = "descr",
        modifiedValue = "a modified contact",
        listSuffix = null,
        encrypted = true,
    )

    val healthElement = CrudSpec(
        name = "HealthElement",
        path = "/rest/v2/helement",
        database = HEALTHDATA,
        create = { id -> mapOf("id" to id, "descr" to "a health element") },
        modifiedField = "descr",
        modifiedValue = "a modified health element",
        listSuffix = null,
        encrypted = true,
    )

    val document = CrudSpec(
        name = "Document",
        path = "/rest/v2/document",
        database = HEALTHDATA,
        create = { id -> mapOf("id" to id, "name" to "a document") },
        modifiedField = "name",
        modifiedValue = "a modified document",
        listSuffix = null,
        encrypted = true,
    )

    val form = CrudSpec(
        name = "Form",
        path = "/rest/v2/form",
        database = HEALTHDATA,
        create = { id -> mapOf("id" to id, "descr" to "a form") },
        modifiedField = "descr",
        modifiedValue = "a modified form",
        listSuffix = null,
        encrypted = true,
    )

    val nonEncrypted = listOf(healthcareParty, user, code, insurance, medicalLocation)
    val encrypted = listOf(patient, contact, healthElement, document, form)
    val all = nonEncrypted + encrypted
}
