/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.entities.embed

import java.io.Serializable
import org.taktik.icure.entities.base.EnumVersion

/**
 * Created by aduchate on 21/01/13, 14:56
 */
@EnumVersion(1L)
enum class Gender(val code: String) : Serializable {
	male("M"), female("F"), indeterminate("I"), changed("C"), changedToMale("Y"), changedToFemale("X"), unknown("U");

	override fun toString(): String {
		return code
	}

	companion object {
		fun fromCode(code: String?): Gender? {
			return code?.let { c -> values().find { c == it.code } }
		}
	}
}
