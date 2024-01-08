/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.rest.v1.dto.filter.predicate

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.taktik.icure.services.external.rest.v2.handlers.JacksonPredicateDeserializer
import java.io.Serializable

@JsonDeserialize(using = JacksonPredicateDeserializer::class)
interface Predicate : Serializable
