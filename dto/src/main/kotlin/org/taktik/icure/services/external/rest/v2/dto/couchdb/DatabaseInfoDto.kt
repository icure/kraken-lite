/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.rest.v2.dto.couchdb

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class DatabaseInfoDto(
	val id: String,
	val updateSeq: String?,
	val fileSize: Long?,
	val externalSize: Long?,
	val activeSize: Long?,
	val docs: Long?,
	val q: Int?,
	val n: Int?,
	val w: Int?,
	val r: Int?
) : Serializable
