package org.taktik.icure.properties

import java.net.URI

interface CouchDbProperties {
    var url: String
    var altUrls: String
    var username: String?
    var password: String?

    fun knownServerUrls() = if (altUrls.isBlank()) listOf(url) else altUrls.split(";").let { if (it.contains(url)) it else listOf(url) + it }
    fun knownServerUris() = knownServerUrls().map { URI(it) }
    fun preferredServerUrlForNewlyRegisteredDatabase() = knownServerUrls().last()
}