/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.rest.v1.dto.base

import java.io.Serializable

interface IdentifiableDto<T> : Serializable {
	val id: T
}
