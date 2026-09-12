package org.taktik.icure.e2e

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.maps.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/**
 * Startup behaviour of kraken-lite, which is where it diverges most from kraken-cloud: it owns its CouchDB,
 * creates its own databases and design documents, and bootstraps its own admin user.
 *
 * Each scenario boots its own process. That is the whole point: this behaviour only happens once, at
 * startup, under a given set of properties, so the only way to observe it is to start again.
 */
class InitializationE2eTest : StringSpec({

	"A single node couchdb is required by the lite dao" {
		// ICureLiteDAOImpl.checkOnlyLocalNodeExists throws when the cluster has more than one node, and the
		// startup runner writes the couchdb config, so a multi node database would fail the boot outright.
		CouchDbAdmin.nodeCount() shouldBe 1
	}

	"Starting on a new database should create every database and design document" {
		CouchDbAdmin.wipeIcureDatabases()

		withKrakenLite("new-database") { instance ->
			CouchDbAdmin.allDatabases() shouldContainAll E2eConfig.expectedDatabases

			// Built in design documents are created for the main partition of every generic dao.
			CouchDbAdmin.designDocumentIds("${E2eConfig.DB_PREFIX}-base").shouldNotBeEmpty()

			instance.client().get("/actuator/health").orFail("Health").json["status"].asText() shouldBe "UP"
			instance.logs() shouldContain "is started"
		}
	}

	"The admin user should be bootstrapped only once" {
		CouchDbAdmin.wipeIcureDatabases()

		val firstPassword = withKrakenLite("admin-bootstrap-first") { instance ->
			instance.adminClient().get("/rest/v2/user?limit=100").orFail("Listing users")
			requireNotNull(instance.adminPassword) { "The admin user should have been created on a new database" }
		}

		// Second boot on the same database: the `listUsers(...).isEmpty()` guard must stop it creating
		// another admin, otherwise every restart would add a user and log a new password.
		withKrakenLite("admin-bootstrap-second") { instance ->
			instance.adminPassword shouldBe null

			val logins = instance.client()
				.withBearer(instance.client().login(KrakenLiteInstance.ADMIN_LOGIN, firstPassword))
				.get("/rest/v2/user?limit=100").orFail("Listing users")
				.json["rows"].map { it["login"].asText() }

			logins.count { it == KrakenLiteInstance.ADMIN_LOGIN } shouldBe 1
		}
	}

	"Restarting on an existing database should not create new design documents" {
		CouchDbAdmin.wipeIcureDatabases()

		val database = "${E2eConfig.DB_PREFIX}-base"

		// Only the main partition is considered: it is created synchronously by the startup runner before
		// the application reports itself started, whereas the Maurice and DataOwner partitions are created
		// on GlobalScope and are still being written when the instance is shut down.
		fun mainPartitionRevisions() = CouchDbAdmin.designDocumentIds(database)
			.filterNot { it.contains("-Maurice") || it.contains("-DataOwner") }
			.associateWith { id -> CouchDbAdmin.designDocument(database, id)?.get("_rev")?.asText() }

		val afterFirstBoot = withKrakenLite("existing-database-first") { mainPartitionRevisions() }
		afterFirstBoot.shouldNotBeEmpty()

		withKrakenLite("existing-database-second") {
			val afterSecondBoot = mainPartitionRevisions()

			// The expensive regression would be a new design document id appearing, since that is what
			// forces CouchDB to build a fresh index. `ignoreIfUnchanged` is what prevents it.
			afterSecondBoot.keys shouldBe afterFirstBoot.keys

			// Every design document keeps its revision, except the two whose views the startup path
			// actually queries - the admin bootstrap lists users, and that resolution path rewrites
			// _design/User and _design/HealthcareParty, taking them from rev 1 to rev 2 on each restart.
			// The view code is unchanged, so CouchDB keeps the existing index; this is churn, not a reindex.
			val rewritten = afterSecondBoot.filter { (id, rev) -> afterFirstBoot[id] != rev }.keys
			rewritten.all { it.startsWith("_design/User_") || it.startsWith("_design/HealthcareParty_") } shouldBe true
		}
	}

	"Every registered dao should get at least one design document" {
		CouchDbAdmin.wipeIcureDatabases()

		withKrakenLite("design-docs-per-dao") {
			val entities = DaoCatalog.entityNames
			// Guards the discovery itself: if the reflection stopped finding DAOs the test would otherwise
			// pass vacuously.
			(entities.size > 30) shouldBe true

			// The main partition documents are written synchronously by the startup runner, but the
			// internal DAOs and the partitioned ones are not, so give them a chance to appear.
			waitUntil("a design document for every dao") {
				missingDesignDocuments(entities).isEmpty()
			}

			missingDesignDocuments(entities) shouldBe emptyList()

			// Sanity check on the matcher itself, so that a bug making everything "found" - an over eager
			// prefix match, say - cannot turn the assertion above into a no-op.
			missingDesignDocuments(listOf("NotAnEntity")) shouldBe listOf("NotAnEntity")
		}
	}

	"Starting with the builtin views disabled should not create any design document" {
		CouchDbAdmin.wipeIcureDatabases()

		withKrakenLite(
			"no-builtin-views",
			mapOf(
				"icure.dao.indexBuiltInViews" to "false",
				// The admin bootstrap MUST be off here: it calls userLogic.listUsers, which needs
				// _design/User, which is exactly what indexBuiltInViews=false stops the runner from
				// creating. With both enabled on an empty database the context fails to start with
				// "No design doc for _design/User can be found at this time". See the note below.
				"icure.authentication.lite.createAdminUser" to "false",
			),
		) {
			// Databases are created lazily, by the first dispatcher that opens a client on them. With no
			// design document to write, only the families the startup path actually touches show up - the
			// patient and healthdata ones are not created at all until an entity needs them.
			CouchDbAdmin.allDatabases() shouldContainAll listOf(
				"${E2eConfig.DB_PREFIX}-base",
				"${E2eConfig.DB_PREFIX}-system",
			)

			// No design document is created for the entity databases.
			CouchDbAdmin.designDocumentIds("${E2eConfig.DB_PREFIX}-base") shouldBe emptyList()
		}
	}

	"Disabling the builtin views while bootstrapping the admin user should fail to start" {
		// Documents a real interaction rather than asserting a desirable behaviour: on an empty database
		// these two options are mutually exclusive, and the failure surfaces as an opaque design document
		// error rather than a configuration error. The README presents indexBuiltInViews=false as something
		// to combine with a custom views repository, which is the configuration that does work.
		CouchDbAdmin.wipeIcureDatabases()

		val failure = runCatching {
			withKrakenLite(
				"no-builtin-views-with-admin",
				mapOf(
					"icure.dao.indexBuiltInViews" to "false",
					"icure.authentication.lite.createAdminUser" to "true",
				),
			) {}
		}.exceptionOrNull()

		failure?.message shouldContain "No design doc for _design/User can be found at this time"
	}

	"Custom design documents should be created from the configured views repository" {
		CouchDbAdmin.wipeIcureDatabases()

		ViewsRepoStub.start(
			mapOf("code" to mapOf("all" to "function(doc) { if (doc.java_type == 'Code') emit(null, doc._id) }")),
		).use { repo ->
			withKrakenLite(
				"custom-design-docs",
				mapOf(
					"icure.designdoc.lite.builtinViewsRepository" to repo.repoUrl,
					"icure.designdoc.lite.views-by-entity.Code" to "all",
				),
			) {
				// LiteDesignDocSchemaProvider allocates one partition per view: _design/<Entity>-<n>. The
				// schema is generated on GlobalScope, after the partitioned design documents, so it appears
				// some time after the application reports itself started.
				waitUntil("_design/Code-1 to be created") {
					CouchDbAdmin.designDocumentIds("${E2eConfig.DB_PREFIX}-base").contains("_design/Code-1")
				}

				CouchDbAdmin.designDocument("${E2eConfig.DB_PREFIX}-base", "_design/Code-1")
					?.get("views")?.fieldNames()?.asSequence()?.toList() shouldBe listOf("all")
			}
		}
	}

	"A second boot should reuse the existing custom design document partition" {
		CouchDbAdmin.wipeIcureDatabases()

		val properties = { repoUrl: String ->
			mapOf(
				"icure.designdoc.lite.builtinViewsRepository" to repoUrl,
				"icure.designdoc.lite.views-by-entity.Code" to "all",
			)
		}

		ViewsRepoStub.start(
			mapOf("code" to mapOf("all" to "function(doc) { if (doc.java_type == 'Code') emit(null, doc._id) }")),
		).use { repo ->
			withKrakenLite("custom-design-docs-first", properties(repo.repoUrl)) {
				waitUntil("_design/Code-1 to be created on the first boot") {
					CouchDbAdmin.designDocumentIds("${E2eConfig.DB_PREFIX}-base").contains("_design/Code-1")
				}
			}

			withKrakenLite("custom-design-docs-second", properties(repo.repoUrl)) {
				// Give the second boot the same chance to allocate a new partition, then assert it did not.
				waitUntil("the schema to be regenerated on the second boot") {
					CouchDbAdmin.designDocumentIds("${E2eConfig.DB_PREFIX}-base").contains("_design/Code-1")
				}
				delay(5.seconds)

				val codeDesignDocs = CouchDbAdmin.designDocumentIds("${E2eConfig.DB_PREFIX}-base")
					.filter { it.matches("^_design/Code-\\d+$".toRegex()) }

				// Reusing the partition index is what stops a restart from allocating _design/Code-2 and
				// reindexing the view from scratch.
				codeDesignDocs shouldBe listOf("_design/Code-1")
			}
		}
	}

	"Dropping a view from the configuration should leave its design document in place" {
		CouchDbAdmin.wipeIcureDatabases()

		val base = "${E2eConfig.DB_PREFIX}-base"

		// Two views to start with, so the schema provider allocates two partitions for Code.
		ViewsRepoStub.start(
			mapOf(
				"code" to mapOf(
					"all" to "function(doc) { if (doc.java_type == 'Code') emit(null, doc._id) }",
					"by_type" to "function(doc) { if (doc.java_type == 'Code') emit(doc.type, doc._id) }",
				),
			),
		).use { repo ->
			fun properties(views: String) = mapOf(
				"icure.designdoc.lite.builtinViewsRepository" to repo.repoUrl,
				"icure.designdoc.lite.views-by-entity.Code" to views,
			)

			withKrakenLite("dropped-view-first", properties("all,by_type")) {
				waitUntil("both Code partitions to be created") {
					CouchDbAdmin.designDocumentIds(base).containsAll(listOf("_design/Code-1", "_design/Code-2"))
				}
			}

			// Restart with by_type no longer configured. The schema built in memory now only knows about
			// `all`, but the design document backing the dropped view must survive: deleting it would throw
			// away an index that is expensive to rebuild, and would break any instance still querying it.
			withKrakenLite("dropped-view-second", properties("all")) {
				waitUntil("the schema to be regenerated for the remaining view") {
					CouchDbAdmin.designDocumentIds(base).contains("_design/Code-1")
				}
				// Give the startup path time to do the wrong thing before concluding that it did not.
				delay(10.seconds)

				val codeDesignDocs = CouchDbAdmin.designDocumentIds(base)
					.filter { it.matches("^_design/Code-\\d+$".toRegex()) }
					.sorted()

				codeDesignDocs shouldBe listOf("_design/Code-1", "_design/Code-2")

				// The orphaned design document must still carry its view, not just exist as an empty shell.
				CouchDbAdmin.designDocument(base, "_design/Code-2")
					?.get("views")?.fieldNames()?.asSequence()?.toList() shouldBe listOf("by_type")
			}
		}
	}

	"The number of background indexation workers should be written to the couchdb config" {
		CouchDbAdmin.wipeIcureDatabases()

		withKrakenLite("indexation-workers", mapOf("icure.dao.backgroundIndexationWorkers" to "3")) { instance ->
			instance.adminClient().get("/rest/v2/icure/couchdb/config/ken/batch_channels")
				.orFail("Reading the couchdb config")
				.body.trim('"') shouldBe "3"
		}
	}

	"Protected endpoints should require authentication while the public ones should not" {
		CouchDbAdmin.wipeIcureDatabases()

		withKrakenLite("authentication") { instance ->
			val anonymous = instance.client()

			anonymous.get("/rest/v2/user?limit=1").status shouldBe 401
			anonymous.get("/rest/v2/hcparty?limit=1").status shouldBe 401

			// The permit-all list of LiteSecurityConfig.
			anonymous.get("/actuator/health").isSuccess() shouldBe true
			anonymous.get("/rest/v2/icure/v").isSuccess() shouldBe true

			instance.adminClient().get("/rest/v2/user?limit=1").isSuccess() shouldBe true
		}
	}
})

/**
 * Entities from [DaoCatalog] that have no design document in any of the databases.
 *
 * Matching is anchored on the separators the naming convention uses - `_design/<Entity>_<hash>` for the main
 * partition and `_design/<Entity>-<partition>_<hash>` for the others - so that, say, `_design/FormTemplate_x`
 * is never mistaken for a design document of `Form`.
 */
private fun missingDesignDocuments(entities: List<String>): List<String> {
	val designDocIds = CouchDbAdmin.allDesignDocumentIds()
	return entities.filterNot { entity ->
		designDocIds.any { it.startsWith("_design/${entity}_") || it.startsWith("_design/${entity}-") }
	}
}
