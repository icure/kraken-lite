/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.domain.filter.healthelement

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.HealthElement

interface HealthElementByHcPartyTagCodeFilter : Filter<String, HealthElement> {
	val healthcarePartyId: String?
	val codeType: String?
	val codeCode: String?
	val tagType: String?
	val tagCode: String?
	val status: Int?
}
