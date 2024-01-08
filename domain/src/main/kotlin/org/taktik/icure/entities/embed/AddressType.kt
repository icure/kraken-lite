/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.entities.embed

import java.io.Serializable
import org.taktik.icure.entities.base.EnumVersion

/**
 * Created by aduchate on 21/01/13, 14:53
 */
@EnumVersion(1L)
enum class AddressType : Serializable {
	home, work, vacation, hospital, clinic, hq, other, temporary, postal, diplomatic, reference, careaddress
}
