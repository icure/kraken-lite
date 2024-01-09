package org.taktik.icure.application.utils

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.taktik.icure.applications.utils.JarUtils

@Service
@Profile("app")
class KrakenLiteJarUtils : JarUtils()
