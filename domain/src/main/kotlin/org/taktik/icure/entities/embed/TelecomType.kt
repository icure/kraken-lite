/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.entities.embed

import java.io.Serializable
import org.taktik.icure.entities.base.EnumVersion

/**
 * Created by aduchate on 21/01/13, 14:50
 */
@EnumVersion(1L)
enum class TelecomType : Serializable {
	mobile, phone, email, fax, skype, im, medibridge, ehealthbox, apicrypt, web, print, disk, other, pager
}
