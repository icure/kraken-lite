package org.taktik.icure.asyncdao.impl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import org.taktik.couchdb.Client
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.annotation.Views
import org.taktik.couchdb.entity.DesignDocument
import org.taktik.icure.asyncdao.Partitions
import org.taktik.icure.config.ExternalViewsConfig
import org.taktik.icure.entities.Contact
import org.taktik.icure.properties.CouchDbPropertiesImpl

private const val ALL_MAP = "function(doc) { if (doc.java_type == 'Contact') emit(null, doc._id) }"
private const val BY_CODE_MAP = "function(doc) { emit(doc.code, doc._id) }"

/**
 * The metadata source: [org.taktik.couchdb.support.SimpleViewGenerator] reads the `@View` annotations off
 * the runtime class, exactly as it does for a real DAO.
 */
@Views(
	View(name = "all", map = ALL_MAP),
	View(name = "by_code", map = BY_CODE_MAP, secondaryPartition = "Maurice"),
)
private class ContactMetaDataSource

@Views(View(name = "all", map = ALL_MAP))
private class MainOnlyMetaDataSource

/**
 * [LiteDesignDocumentProvider] is the lite-only [org.taktik.couchdb.dao.DesignDocumentProvider]. The
 * behaviour pinned here is what keeps a restart cheap: design documents whose views are unchanged must be
 * recognised as such and skipped, otherwise every boot would rewrite them and trigger a full reindex.
 */
class LiteDesignDocumentProviderTest : StringSpec({

	fun provider() = LiteDesignDocumentProvider(
		couchDbProperties = CouchDbPropertiesImpl(),
		externalViewsLoader = null,
		externalViewsConfig = ExternalViewsConfig(),
	)

	/** A client that reports [existing] as the design documents already stored in the database. */
	fun clientWith(existing: List<DesignDocument>) = mockk<Client>().also { client ->
		coEvery { client.designDocumentsIds() } returns existing.map { it.id }.toSet()
		coEvery { client.get(any<String>(), DesignDocument::class.java) } answers {
			existing.firstOrNull { it.id == firstArg<String>() }
		}
	}

	"The base design document id should follow the naming convention" {
		with(provider()) {
			baseDesignDocumentId(Contact::class.java, null) shouldBe "_design/Contact"
			baseDesignDocumentId(Contact::class.java, "") shouldBe "_design/Contact-"
			baseDesignDocumentId(Contact::class.java, "Maurice") shouldBe "_design/Contact-Maurice"
		}
	}

	"Generating for the main partition should exclude the secondary partitions" {
		val generated = provider().generateDesignDocuments(
			entityClass = Contact::class.java,
			metaDataSource = ContactMetaDataSource(),
			client = null,
			partition = Partitions.Main,
			ignoreIfUnchanged = false,
		)

		generated shouldHaveSize 1
		generated.single().views.keys shouldContainExactlyInAnyOrder listOf("all")
		// Versioned ids carry a hash suffix: _design/Contact_<8 hex chars>
		generated.single().id.matches("^_design/Contact_[0-9a-f]{8}$".toRegex()) shouldBe true
	}

	"Generating for a secondary partition should only keep that partition" {
		val generated = provider().generateDesignDocuments(
			entityClass = Contact::class.java,
			metaDataSource = ContactMetaDataSource(),
			client = null,
			partition = Partitions.Maurice,
			ignoreIfUnchanged = false,
		)

		generated shouldHaveSize 1
		generated.single().views.keys shouldContainExactlyInAnyOrder listOf("by_code")
		generated.single().id.matches("^_design/Contact-Maurice_[0-9a-f]{8}$".toRegex()) shouldBe true
	}

	"Generating for all partitions should keep every design document" {
		provider().generateDesignDocuments(
			entityClass = Contact::class.java,
			metaDataSource = ContactMetaDataSource(),
			client = null,
			partition = Partitions.All,
			ignoreIfUnchanged = false,
		).map { it.id.substringBeforeLast('_') }.toSet() shouldBe setOf("_design/Contact", "_design/Contact-Maurice")
	}

	"A partition with no matching view should generate nothing" {
		provider().generateDesignDocuments(
			entityClass = Contact::class.java,
			metaDataSource = MainOnlyMetaDataSource(),
			client = null,
			partition = Partitions.Maurice,
			ignoreIfUnchanged = false,
		).shouldBeEmpty()
	}

	"An unchanged design document should be skipped when unchanged documents are ignored" {
		val current = provider().generateDesignDocuments(
			entityClass = Contact::class.java,
			metaDataSource = MainOnlyMetaDataSource(),
			client = null,
			partition = Partitions.Main,
			ignoreIfUnchanged = false,
		).single()

		provider().generateDesignDocuments(
			entityClass = Contact::class.java,
			metaDataSource = MainOnlyMetaDataSource(),
			client = clientWith(listOf(current)),
			partition = Partitions.Main,
			ignoreIfUnchanged = true,
		).shouldBeEmpty()
	}

	"An unchanged design document should still be regenerated when unchanged documents are not ignored" {
		val current = provider().generateDesignDocuments(
			entityClass = Contact::class.java,
			metaDataSource = MainOnlyMetaDataSource(),
			client = null,
			partition = Partitions.Main,
			ignoreIfUnchanged = false,
		).single()

		provider().generateDesignDocuments(
			entityClass = Contact::class.java,
			metaDataSource = MainOnlyMetaDataSource(),
			client = clientWith(listOf(current)),
			partition = Partitions.Main,
			ignoreIfUnchanged = true.not(),
		) shouldHaveSize 1
	}

	"A design document differing only in formatting should be considered unchanged" {
		val current = provider().generateDesignDocuments(
			entityClass = Contact::class.java,
			metaDataSource = MainOnlyMetaDataSource(),
			client = null,
			partition = Partitions.Main,
			ignoreIfUnchanged = false,
		).single()

		// Same code, different whitespace: View.normalizedMap strips it, so the documents are equipollent.
		val reformatted = current.copy(
			views = current.views.mapValues { (_, view) ->
				view.copy(map = view.map.replace(" ", "\n\t  "))
			},
		)

		provider().generateDesignDocuments(
			entityClass = Contact::class.java,
			metaDataSource = MainOnlyMetaDataSource(),
			client = clientWith(listOf(reformatted)),
			partition = Partitions.Main,
			ignoreIfUnchanged = true,
		).shouldBeEmpty()
	}

	"A design document with a genuinely different map should be regenerated" {
		val current = provider().generateDesignDocuments(
			entityClass = Contact::class.java,
			metaDataSource = MainOnlyMetaDataSource(),
			client = null,
			partition = Partitions.Main,
			ignoreIfUnchanged = false,
		).single()

		val changed = current.copy(
			views = current.views.mapValues { (_, view) ->
				view.copy(map = "function(doc) { emit(doc.somethingElse, doc._id) }")
			},
		)

		provider().generateDesignDocuments(
			entityClass = Contact::class.java,
			metaDataSource = MainOnlyMetaDataSource(),
			client = clientWith(listOf(changed)),
			partition = Partitions.Main,
			ignoreIfUnchanged = true,
		) shouldHaveSize 1
	}

	"A design document with a different reduce should be regenerated" {
		val current = provider().generateDesignDocuments(
			entityClass = Contact::class.java,
			metaDataSource = MainOnlyMetaDataSource(),
			client = null,
			partition = Partitions.Main,
			ignoreIfUnchanged = false,
		).single()

		val changed = current.copy(
			views = current.views.mapValues { (_, view) -> view.copy(reduce = "_count") },
		)

		provider().generateDesignDocuments(
			entityClass = Contact::class.java,
			metaDataSource = MainOnlyMetaDataSource(),
			client = clientWith(listOf(changed)),
			partition = Partitions.Main,
			ignoreIfUnchanged = true,
		) shouldHaveSize 1
	}

	"A design document that does not exist yet should always be generated" {
		provider().generateDesignDocuments(
			entityClass = Contact::class.java,
			metaDataSource = MainOnlyMetaDataSource(),
			client = clientWith(emptyList()),
			partition = Partitions.Main,
			ignoreIfUnchanged = true,
		) shouldHaveSize 1
	}

	"currentDesignDocumentId should return the versioned id of the requested partition" {
		with(provider()) {
			currentDesignDocumentId(Contact::class.java, ContactMetaDataSource(), null)
				.matches("^_design/Contact_[0-9a-f]{8}$".toRegex()) shouldBe true
			currentDesignDocumentId(Contact::class.java, ContactMetaDataSource(), "Maurice")
				.matches("^_design/Contact-Maurice_[0-9a-f]{8}$".toRegex()) shouldBe true
		}
	}

	"currentDesignDocumentId should depend only on the views of its own partition" {
		with(provider()) {
			val first = currentDesignDocumentId(Contact::class.java, MainOnlyMetaDataSource(), null)
			currentDesignDocumentId(Contact::class.java, MainOnlyMetaDataSource(), null) shouldBe first

			// ContactMetaDataSource declares the same "all" view in the main partition plus a Maurice view.
			// The main design document must keep the same version hash: a change confined to another
			// partition must not invalidate it, otherwise it would be reindexed for nothing.
			currentDesignDocumentId(Contact::class.java, ContactMetaDataSource(), null) shouldBe first
		}
	}

	"External design documents should be empty when no external views loader is configured" {
		provider().generateExternalDesignDocuments(
			entityClass = Contact::class.java,
			partitionsWithRepo = mapOf("custom" to "https://example.com/repo"),
			client = null,
			ignoreIfUnchanged = false,
		).shouldBeEmpty()
	}
})
