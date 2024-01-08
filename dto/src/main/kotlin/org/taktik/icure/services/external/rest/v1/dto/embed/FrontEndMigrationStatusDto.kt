/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable
import org.taktik.icure.services.external.rest.v1.dto.base.EnumVersionDto

@EnumVersionDto(1L)

enum class FrontEndMigrationStatusDto : Serializable {
	STARTED, PAUSED, ERROR, SUCCESS
}
