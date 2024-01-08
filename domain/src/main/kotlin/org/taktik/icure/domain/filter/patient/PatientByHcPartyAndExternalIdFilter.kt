/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.domain.filter.patient

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.Patient

interface PatientByHcPartyAndExternalIdFilter : Filter<String, Patient> {
	val externalId: String?
	val healthcarePartyId: String?
}
