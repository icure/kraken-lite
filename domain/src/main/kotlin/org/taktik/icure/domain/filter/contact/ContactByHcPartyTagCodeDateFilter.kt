/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.domain.filter.contact

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.Contact

interface ContactByHcPartyTagCodeDateFilter : Filter<String, Contact> {
	val healthcarePartyId: String?
	val tagType: String?
	val tagCode: String?
	val codeType: String?
	val codeCode: String?
	val startOfContactOpeningDate: Long?
	val endOfContactOpeningDate: Long?
}
