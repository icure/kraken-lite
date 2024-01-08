/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.entities.base

/**
 *
 * @property tags tags that qualify this entity as being member of a certain class.
 *
 */
interface HasTags {
	val tags: Set<CodeStub>
}
