/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.domain.filter.impl.predicate

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.couchdb.id.Identifiable
import org.taktik.icure.domain.filter.predicate.Predicate

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class AlwaysPredicate : Predicate, Serializable {
	override fun apply(input: Identifiable<String>) = true
}
