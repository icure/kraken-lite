/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

interface CouchDbLiteProperties : CouchDbProperties {
    var designDocumentStatusCheckTimeoutMilliseconds: Long
    var cachedDesignDocumentTtlMinutes: Long
    var populateDatabaseFromLocalXmls: Boolean
    var prefix: String
    val skipDesignDocumentUpdate: Boolean
}

@Component
@ConfigurationProperties("icure.couchdb")
data class CouchDbPropertiesImpl(
    override var url: String = "http://127.0.0.1:5984",
    override var altUrls: String = "",
    override var username: String? = "icure",
    override var password: String? = "icure",
    override var cachedDesignDocumentTtlMinutes: Long = 15,
    override var designDocumentStatusCheckTimeoutMilliseconds: Long = 2000,
    override var populateDatabaseFromLocalXmls: Boolean = true,
    override var prefix: String = "icure"
) : CouchDbLiteProperties {
    override val skipDesignDocumentUpdate: Boolean
        get() = url.contains("couch-cluster")
}
