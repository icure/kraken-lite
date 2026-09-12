package org.taktik.icure.e2e

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeBlank
import org.taktik.icure.e2e.crud.base.CrudSpec
import org.taktik.icure.e2e.crud.base.Entities

/**
 * CRUD over the real HTTP API of a real kraken-lite jar.
 *
 * A single instance is shared by the whole spec: unlike the initialization scenarios, nothing here depends
 * on how the process started, so booting once keeps the suite usable.
 *
 * The encrypted entities carry a fake delegation (see Delegations.kt). That is enough to exercise the
 * server side path - routing to the right database family, storing, reading back - without reimplementing
 * the SDK's cryptography.
 */
class CrudE2eTest : StringSpec({

    var instance: KrakenLiteInstance? = null
    lateinit var client: E2eHttpClient
    lateinit var dataOwnerId: String

    beforeSpec {
        CouchDbAdmin.wipeIcureDatabases()
        instance = KrakenLiteInstance.start("crud")
        client = instance.adminClient()

        // The delegations reference a real healthcare party, which doubles as the data owner of every
        // encrypted entity created below.
        dataOwnerId = "hcp-${uuid()}"
        client.post(Entities.healthcareParty.path, mapOf("id" to dataOwnerId, "firstName" to "Data", "lastName" to "Owner"))
            .orFail("Creating the data owner")
    }

    afterSpec {
        instance?.close()
    }

    fun newId(spec: CrudSpec) = if (spec.name == "Code") "e2e|${uuid().take(8)}|1" else "${spec.name.lowercase()}-${uuid()}"

    /** Creates an entity and returns its id and the created body. */
    fun create(spec: CrudSpec): Pair<String, Map<String, Any?>> {
        val id = newId(spec)
        val created = client.post(spec.path, spec.createPayload(id, dataOwnerId))
            .orFail("Creating a ${spec.name}")
        @Suppress("UNCHECKED_CAST")
        return id to objectMapper.convertValue(created.json, Map::class.java) as Map<String, Any?>
    }

    Entities.all.forEach { spec ->

        "${spec.name} should be created with an id and a revision" {
            val (id, created) = create(spec)

            created["id"] shouldBe id
            (created["rev"] as String).shouldNotBeBlank()
        }

        "${spec.name} should be stored in ${spec.database}" {
            val (id, _) = create(spec)

            // This is the assertion that actually covers the lite specific database split: each dispatcher
            // opens "$prefix-$dbFamily", and nothing else in the test suite checks that the entity landed
            // in the family its DAO was wired to.
            CouchDbAdmin.documentCount(spec.database, id, id) shouldBe 1
        }

        "${spec.name} should be readable back by id" {
            val (id, created) = create(spec)

            val fetched = client.get(spec.getPath(id)).orFail("Getting a ${spec.name}").json
            fetched["id"].asText() shouldBe id
            fetched["rev"].asText() shouldBe created["rev"]
        }

        "${spec.name} should be modifiable" {
            val (id, created) = create(spec)

            val modified = client.put(spec.path, created + mapOf(spec.modifiedField to spec.modifiedValue))
                .orFail("Modifying a ${spec.name}").json

            modified[spec.modifiedField].asText() shouldBe spec.modifiedValue
            // A new revision proves the change reached CouchDB rather than just being echoed back.
            modified["rev"].asText() shouldNotBe created["rev"]

            client.get(spec.getPath(id)).orFail("Re-reading a ${spec.name}")
                .json[spec.modifiedField].asText() shouldBe spec.modifiedValue
        }

        "${spec.name} should be deletable" {
            val (id, created) = create(spec)

            spec.delete(client, id, created["rev"] as String).orFail("Deleting a ${spec.name}")

            // Deletion is a soft delete: the document stays but comes back marked as deleted.
            val afterDelete = client.get(spec.getPath(id))
            if (afterDelete.isSuccess()) {
                afterDelete.json["deletionDate"].isNull shouldBe false
            } else {
                (afterDelete.status == 404) shouldBe true
            }
        }

        if (spec.listSuffix != null) {
            "${spec.name} should appear in the listing" {
                val (id, _) = create(spec)

                val listed = client.get("${spec.path}${spec.listSuffix}").orFail("Listing ${spec.name}").json
                val rows = listed["rows"] ?: listed
                rows.any { it["id"].asText() == id } shouldBe true
            }
        }
    }
})
