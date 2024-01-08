/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.entities.embed

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class AgreementAppendix(
	val docSeq: Int? = null,
	val verseSeq: Int? = null,
	val documentId: String? = null,
	val path: String? = null
) : Serializable
