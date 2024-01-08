/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.dao.migration

interface DbMigration {
    fun hasBeenApplied(): Boolean
    fun apply()
}
