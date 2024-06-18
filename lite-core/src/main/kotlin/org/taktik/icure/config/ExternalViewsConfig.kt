package org.taktik.icure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("icure.couchdb.external")
class ExternalViewsConfig {

	var repos: Map<String, String> = emptyMap()

}