/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.domain.filter.service

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.embed.Service

interface ServiceBySecretForeignKeys : Filter<String, Service> {
	val healthcarePartyId: String?
	val patientSecretForeignKeys: Set<String>
}
