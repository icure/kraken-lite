/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.domain.filter.contact

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.Contact

interface ContactByServiceIdsFilter : Filter<String, Contact> {
	val ids: List<String>?
}
