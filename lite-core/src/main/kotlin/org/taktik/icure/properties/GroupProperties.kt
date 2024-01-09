package org.taktik.icure.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

/**
 * Allows to configure basic information on the group used by the lite instance. This allows to replicate in a limited
 * way the behavior of some methods of iCure cloud (e.g. user/matches), improving the experience on the client side.
 */
@Component
@Profile("app")
@ConfigurationProperties("icure.lite.group")
data class GroupProperties(
    var name: String? = null,
    var id: String? = null,
)
