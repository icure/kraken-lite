/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.domain.filter.impl.predicate

import org.taktik.couchdb.id.Identifiable
import org.taktik.icure.domain.filter.predicate.Predicate

data class AndPredicate(val predicates: List<Predicate> = listOf()) : Predicate {
	override fun apply(input: Identifiable<String>): Boolean {
		for (p in predicates) {
			if (!p.apply(input)) {
				return false
			}
		}
		return true
	}
}
