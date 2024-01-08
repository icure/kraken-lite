/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.domain.filter.invoice

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.Invoice

interface InvoiceByHcPartyCodeDateFilter : Filter<String, Invoice> {
	val healthcarePartyId: String?
	val code: String?
	val startInvoiceDate: Long?
	val endInvoiceDate: Long?
}
