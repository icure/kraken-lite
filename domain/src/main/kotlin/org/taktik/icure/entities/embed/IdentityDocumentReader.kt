/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.entities.embed

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class IdentityDocumentReader(
	val justificatifDocumentNumber: String? = null,
	val supportSerialNumber: String? = null,
	val timeReadingEIdDocument: Long? = null,
	val eidDocumentSupportType: Int = 0,
	val reasonManualEncoding: Int = 0,
	val reasonUsingVignette: Int = 0
) : Serializable
