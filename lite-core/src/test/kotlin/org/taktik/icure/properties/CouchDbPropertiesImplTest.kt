package org.taktik.icure.properties

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * [CouchDbPropertiesImpl] is the lite-only implementation of [CouchDbLiteProperties]. Its
 * [CouchDbPropertiesImpl.skipDesignDocumentUpdate] heuristic decides whether the whole design document
 * initialization block of the startup runner is executed, so it is worth pinning explicitly.
 */
class CouchDbPropertiesImplTest : StringSpec({

	"Design document update should be skipped only when the couchdb url points to a couch cluster" {
		listOf(
			"http://couch-cluster.icure.cloud" to true,
			"https://couch-cluster:5984" to true,
			"http://a.couch-cluster.b:5984/" to true,
			"http://127.0.0.1:5984" to false,
			"http://localhost:5984" to false,
			"https://couchdb.icure.cloud" to false,
			"" to false,
		).forEach { (url, expected) ->
			CouchDbPropertiesImpl(url = url).skipDesignDocumentUpdate shouldBe expected
		}
	}

	"Default properties should match the values declared in application-app.properties" {
		with(CouchDbPropertiesImpl()) {
			url shouldBe "http://127.0.0.1:5984"
			username shouldBe "icure"
			password shouldBe "icure"
			prefix shouldBe "icure"
			cachedDesignDocumentTtlMinutes shouldBe 15
			designDocumentStatusCheckTimeoutMilliseconds shouldBe 2000
			populateDatabaseFromLocalXmls shouldBe true
			maxPendingAcquire shouldBe 1000
			skipDesignDocumentUpdate shouldBe false
		}
	}

	"The prefix should determine the name of the databases used by the dispatchers" {
		// CouchDbDispatcherImpl builds the database name as "$prefix-$dbFamily"; the system dispatcher
		// always uses the hardcoded "icure" prefix instead (CouchDbLiteConfig.ICURE_PREFIX).
		val properties = CouchDbPropertiesImpl(prefix = "custom")
		listOf("base", "patient", "healthdata").map {
			"${properties.prefix}-$it"
		} shouldBe listOf("custom-base", "custom-patient", "custom-healthdata")
	}
})
