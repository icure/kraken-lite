/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.entities.embed

interface Encrypted {
	val encryptedSelf: String?

	fun solveConflictsWith(other: Encrypted) = mapOf(
		"encryptedSelf" to (this.encryptedSelf ?: other.encryptedSelf)
	)
}
