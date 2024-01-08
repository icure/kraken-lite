/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncservice

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.entities.CalendarItemType

interface CalendarItemTypeService {
	suspend fun createCalendarItemType(calendarItemType: CalendarItemType): CalendarItemType?

	/**
	 * Deletes [CalendarItemType]s in batch.
	 * If the user does not meet the precondition to delete [CalendarItemType]s, an error will be thrown.
	 * If the current user does not have the permission to delete one or more elements in
	 * the batch, then those elements will not be deleted and no error will be thrown.
	 *
	 * @param ids a [List] containing the ids of the [CalendarItemType]s to delete.
	 * @return a [Flow] containing the [DocIdentifier]s of the [CalendarItemType]s that were successfully deleted.
	 */
	fun deleteCalendarItemTypes(ids: List<String>): Flow<DocIdentifier>
	suspend fun getCalendarItemType(calendarItemTypeId: String): CalendarItemType?
	fun getCalendarItemTypes(calendarItemTypeIds: Collection<String>): Flow<CalendarItemType>
	fun getAllCalendarItemTypes(): Flow<CalendarItemType>
	suspend fun modifyCalendarItemType(calendarItemType: CalendarItemType): CalendarItemType?
	fun getAllEntitiesIncludeDelete(): Flow<CalendarItemType>
}
