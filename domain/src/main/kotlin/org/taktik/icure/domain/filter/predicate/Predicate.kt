/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.domain.filter.predicate

import org.taktik.couchdb.id.Identifiable

interface Predicate {
	fun apply(input: Identifiable<String>): Boolean
}
