/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.domain.filter.patient

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.Patient

interface PatientByHcPartyDateOfBirthFilter : Filter<String, Patient> {
	val dateOfBirth: Int
	val healthcarePartyId: String?
}
