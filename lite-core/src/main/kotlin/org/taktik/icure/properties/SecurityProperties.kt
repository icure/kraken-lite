package org.taktik.icure.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("app")
@ConfigurationProperties("icure.security")
data class SecurityProperties(
    var hideServerErrorMessage: Boolean = true
)
