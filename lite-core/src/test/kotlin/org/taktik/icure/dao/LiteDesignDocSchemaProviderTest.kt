package org.taktik.icure.dao

import com.fasterxml.jackson.core.type.TypeReference
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.taktik.couchdb.Client
import org.taktik.couchdb.entity.DesignDocument
import org.taktik.couchdb.entity.View
import org.taktik.icure.asyncdao.CouchDbDAO
import org.taktik.icure.asyncdao.CouchDbDispatcher
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asynclogic.datastore.impl.LocalDatastoreInformation
import org.taktik.icure.config.LiteDAOConfig
import org.taktik.icure.dao.repositories.GitHubRepoDownloader
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.base.Code
import org.taktik.icure.properties.DesignDocSchemaProperties
import java.net.URI

private const val REPO = "https://github.com/icure/kraken-builtin-views"

/** A DAO as seen by [LiteDesignDocSchemaProvider]: it needs both the entity class and the dispatcher. */
private interface TestDao<T : org.taktik.couchdb.id.Identifiable<String>> : GenericDAO<T>, CouchDbDAO

/**
 * [LiteDesignDocSchemaProvider] is the lite-only replacement for the cloud schema provider: instead of
 * asking a central service which view lives in which partition, it creates one `_design/<Entity>-<n>`
 * document per view locally and remembers the mapping in memory.
 *
 * The partition allocation is the delicate part - reusing an existing partition index rather than
 * allocating a new one on every boot is what stops a restart from re-indexing the whole database.
 */
class LiteDesignDocSchemaProviderTest : StringSpec({

	val datastore = LocalDatastoreInformation(URI("http://127.0.0.1:5984"))

	fun viewDescriptor(map: String = "function(doc) { emit(null, doc._id) }", reduce: String? = null) =
		GitHubRepoDownloader.ViewDescriptor(
			map = map,
			reduce = reduce,
			weight = null,
			affinities = emptyList(),
			libResources = emptyMap(),
		)

	/**
	 * Wires a provider over a single mocked couchdb [Client]. [existingDesignDocs] are the documents already
	 * present in the database; every created document is captured in the returned list.
	 */
	fun fixture(
		viewsByEntity: Map<String, List<String>>,
		repoViews: Map<String, Map<String, GitHubRepoDownloader.ViewDescriptor>>,
		existingDesignDocs: List<DesignDocument> = emptyList(),
		builtinViewsRepository: String? = REPO,
		daoEntityClasses: List<Class<*>> = listOf(Contact::class.java),
	): Fixture {
		val client = mockk<Client>()
		val created = mutableListOf<DesignDocument>()

		coEvery { client.designDocumentsIds() } returns existingDesignDocs.map { it.id }.toSet()
		coEvery { client.get(any<String>(), any<TypeReference<DesignDocument>>()) } answers {
			existingDesignDocs.firstOrNull { it.id == firstArg<String>() }
		}
		val toCreate = slot<DesignDocument>()
		coEvery { client.create(capture(toCreate), DesignDocument::class.java, any()) } answers {
			toCreate.captured.copy(rev = "1-created").also { created += it }
		}

		val dispatcher = mockk<CouchDbDispatcher>()
		coEvery { dispatcher.getClient(any(), any()) } returns client

		val daos = daoEntityClasses.map { entityClass ->
			mockk<TestDao<*>>().also {
				every { it.entityClass } returns entityClass as Class<Nothing>
				every { it.couchDbDispatcher } returns dispatcher
			}
		}

		val provider = LiteDesignDocSchemaProvider(
			designDocProperties = DesignDocSchemaProperties(
				builtinViewsRepository = builtinViewsRepository,
				viewsByEntity = viewsByEntity,
			),
			gitHubRepoDownloader = mockk<GitHubRepoDownloader>().also {
				coEvery { it.downloadViewsFromRepo(any()) } returns repoViews
			},
		)
		return Fixture(provider, created, daos, client)
	}

	suspend fun LiteDesignDocSchemaProvider.initialize(daos: List<TestDao<*>>, daoConfig: LiteDAOConfig = LiteDAOConfig()) =
		initializeViewsAndCreateLocalSchema(
			daoList = daos,
			datastoreInformation = datastore,
			daoConfig = daoConfig,
			isIndexing = { false },
		)

	"No schema should be produced when no builtin views repository is configured" {
		val (provider, created, daos, _) = fixture(
			viewsByEntity = mapOf("Contact" to listOf("all")),
			repoViews = mapOf("contact" to mapOf("all" to viewDescriptor())),
			builtinViewsRepository = null,
		)

		provider.initialize(daos)

		provider.getOrRequestSchema(datastore).shouldBeNull()
		created shouldBe emptyList()
	}

	"Every configured view should get its own design document on an empty database" {
		val (provider, created, daos, _) = fixture(
			viewsByEntity = mapOf("Contact" to listOf("all", "conflicts")),
			repoViews = mapOf(
				"contact" to mapOf(
					"all" to viewDescriptor("function(doc) { emit(null, doc._id) }"),
					"conflicts" to viewDescriptor("function(doc) { if (doc._conflicts) emit(null, null) }"),
				),
			),
		)

		provider.initialize(daos)

		created.map { it.id } shouldContainExactlyInAnyOrder listOf("_design/Contact-1", "_design/Contact-2")
		// Each design document holds exactly one view, which is the whole point of the per-view partitioning.
		created.forEach { it.views.size shouldBe 1 }

		val schema = provider.getOrRequestSchema(datastore)
		schema?.viewsByEntity shouldBe mapOf("Contact" to mapOf("all" to 1, "conflicts" to 2))
	}

	"The generated schema should be a committed local schema" {
		val (provider, _, daos, _) = fixture(
			viewsByEntity = mapOf("Contact" to listOf("all")),
			repoViews = mapOf("contact" to mapOf("all" to viewDescriptor())),
		)

		provider.initialize(daos)

		with(provider.getOrRequestSchema(datastore)!!) {
			applicationGroupId shouldBe "LOCAL"
			version shouldBe 0
			committed shouldBe true
			rev.shouldBeNull()
		}
	}

	"An existing design document should be reused instead of recreated" {
		val (provider, created, daos, _) = fixture(
			viewsByEntity = mapOf("Contact" to listOf("all")),
			repoViews = mapOf("contact" to mapOf("all" to viewDescriptor())),
			existingDesignDocs = listOf(
				DesignDocument(id = "_design/Contact-3", views = mapOf("all" to View(map = "function(doc) {}"))),
			),
		)

		provider.initialize(daos)

		created shouldBe emptyList()
		provider.getOrRequestSchema(datastore)?.viewsByEntity shouldBe mapOf("Contact" to mapOf("all" to 3))
	}

	"A new view should be allocated after the highest existing partition index" {
		val (provider, created, daos, _) = fixture(
			viewsByEntity = mapOf("Contact" to listOf("all", "conflicts")),
			repoViews = mapOf(
				"contact" to mapOf(
					"all" to viewDescriptor(),
					"conflicts" to viewDescriptor("function(doc) { if (doc._conflicts) emit(null, null) }"),
				),
			),
			existingDesignDocs = listOf(
				// Note the gap: the next index must come from the maximum, not from the document count.
				DesignDocument(id = "_design/Contact-3", views = mapOf("all" to View(map = "function(doc) {}"))),
			),
		)

		provider.initialize(daos)

		created.map { it.id } shouldBe listOf("_design/Contact-4")
		provider.getOrRequestSchema(datastore)?.viewsByEntity shouldBe
			mapOf("Contact" to mapOf("all" to 3, "conflicts" to 4))
	}

	"Design documents that do not follow the partition naming should be ignored" {
		val (provider, created, daos, _) = fixture(
			viewsByEntity = mapOf("Contact" to listOf("all")),
			repoViews = mapOf("contact" to mapOf("all" to viewDescriptor())),
			existingDesignDocs = listOf(
				// The legacy, non partitioned design documents must not be mistaken for schema partitions.
				DesignDocument(id = "_design/Contact", views = mapOf("all" to View(map = "function(doc) {}"))),
				DesignDocument(id = "_design/Contact_abc123", views = mapOf("all" to View(map = "function(doc) {}"))),
				DesignDocument(id = "_design/Patient-7", views = mapOf("all" to View(map = "function(doc) {}"))),
			),
		)

		provider.initialize(daos)

		created.map { it.id } shouldBe listOf("_design/Contact-1")
	}

	"The view map and reduce from the repository should be written to the design document" {
		val (provider, created, daos, _) = fixture(
			viewsByEntity = mapOf("Contact" to listOf("by_code")),
			repoViews = mapOf(
				"contact" to mapOf(
					"by_code" to viewDescriptor(map = "function(doc) { emit(doc.code, 1) }", reduce = "_sum"),
				),
			),
		)

		provider.initialize(daos)

		with(created.single().views.getValue("by_code")) {
			map shouldBe "function(doc) { emit(doc.code, 1) }"
			reduce shouldBe "_sum"
		}
	}

	"An entity without a matching dao should be rejected" {
		val (provider, _, daos, client) = fixture(
			viewsByEntity = mapOf("Patient" to listOf("all")),
			repoViews = mapOf("patient" to mapOf("all" to viewDescriptor())),
			daoEntityClasses = listOf(Contact::class.java),
		)

		shouldThrow<IllegalStateException> {
			provider.initialize(daos)
		}.message shouldContain "unknown entity class Patient"
	}

	"A view missing from the repository should be rejected" {
		val (provider, _, daos, client) = fixture(
			viewsByEntity = mapOf("Contact" to listOf("not_in_repo")),
			repoViews = mapOf("contact" to mapOf("all" to viewDescriptor())),
		)

		shouldThrow<IllegalStateException> {
			provider.initialize(daos)
		}.message shouldContain "unknown view not_in_repo for entity Contact"
	}

	"Several entities should be handled independently" {
		val (provider, created, daos, _) = fixture(
			viewsByEntity = mapOf("Contact" to listOf("all"), "Code" to listOf("all", "by_type")),
			repoViews = mapOf(
				"contact" to mapOf("all" to viewDescriptor()),
				"code" to mapOf("all" to viewDescriptor(), "by_type" to viewDescriptor("function(doc) { emit(doc.type) }")),
			),
			daoEntityClasses = listOf(Contact::class.java, Code::class.java),
		)

		provider.initialize(daos)

		created.map { it.id } shouldContainExactlyInAnyOrder
			listOf("_design/Contact-1", "_design/Code-1", "_design/Code-2")
		provider.getOrRequestSchema(datastore)?.viewsByEntity shouldBe mapOf(
			"Contact" to mapOf("all" to 1),
			"Code" to mapOf("all" to 1, "by_type" to 2),
		)
	}

	"Warmup should not query any view when no dao is selected for startup indexation" {
		val (provider, _, daos, client) = fixture(
			viewsByEntity = mapOf("Contact" to listOf("all")),
			repoViews = mapOf("contact" to mapOf("all" to viewDescriptor())),
		)

		// Neither forceForegroundIndexation nor viewsToIndexAtStartup mention Contact, so nothing is warmed up.
		provider.initialize(daos, LiteDAOConfig())

		verify(exactly = 0) { client.queryView(any(), any<Class<*>>(), any<Class<*>>(), any<Class<*>>(), any(), any(), any()) }
	}

	// Note: the positive warmup path is deliberately not unit tested. `maybeWarmUpDocs` retries a failing
	// warmup forever (`while (!warmup(...)) { delay(1.seconds) }`), so a stub that does not match the call
	// exactly turns the test into an infinite loop rather than a failure. That path is covered end to end
	// instead, against a real CouchDB.
})

private data class Fixture(
	val provider: LiteDesignDocSchemaProvider,
	val created: MutableList<DesignDocument>,
	val daos: List<TestDao<*>>,
	val client: Client,
)
