package org.taktik.icure.domain.filter.hcparty

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.HealthcareParty

interface HealthcarePartyByNameFilter : Filter<String, HealthcareParty> {
	val desc: String?
	val name: String
	val descending: Boolean?
}
