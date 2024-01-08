/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.domain.filter.service

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.embed.Service

interface ServiceByHcPartyTagCodeDateFilter : Filter<String, Service> {
	val healthcarePartyId: String?
	val patientSecretForeignKey: String?
	val tagType: String?
	val tagCode: String?
	val codeType: String?
	val codeCode: String?
	val startValueDate: Long?
	val endValueDate: Long?
	val descending: Boolean
}
