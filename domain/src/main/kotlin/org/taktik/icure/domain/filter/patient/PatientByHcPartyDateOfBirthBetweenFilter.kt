/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.domain.filter.patient

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.Patient

interface PatientByHcPartyDateOfBirthBetweenFilter : Filter<String, Patient> {
	val maxDateOfBirth: Int?
	val minDateOfBirth: Int?
	val healthcarePartyId: String?
}
