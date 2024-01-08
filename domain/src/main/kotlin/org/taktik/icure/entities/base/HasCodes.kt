/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.entities.base

/**
 *
 * @property codes codes that identify or qualify a particular instance of this entity.
 *
 */
interface HasCodes {
	val codes: Set<CodeStub>
}
