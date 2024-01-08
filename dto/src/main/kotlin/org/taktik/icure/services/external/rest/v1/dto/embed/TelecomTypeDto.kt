/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable
import org.taktik.icure.services.external.rest.v1.dto.base.EnumVersionDto

/**
 * Created by aduchate on 21/01/13, 14:50
 */
@EnumVersionDto(1L)
enum class TelecomTypeDto : Serializable {
	mobile, phone, email, fax, skype, im, medibridge, ehealthbox, apicrypt, web, print, disk, other, pager
}
