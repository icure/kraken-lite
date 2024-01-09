package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.CalendarItemTypeLogic
import org.taktik.icure.asyncservice.CalendarItemTypeService
import org.taktik.icure.entities.CalendarItemType

@Service
class CalendarItemTypeServiceImpl(
    private val calendarItemTypeLogic: CalendarItemTypeLogic
) : CalendarItemTypeService {
    override suspend fun createCalendarItemType(calendarItemType: CalendarItemType): CalendarItemType? = calendarItemTypeLogic.createCalendarItemType(calendarItemType)

    override fun deleteCalendarItemTypes(ids: List<String>): Flow<DocIdentifier> = calendarItemTypeLogic.deleteCalendarItemTypes(ids)

    override suspend fun getCalendarItemType(calendarItemTypeId: String): CalendarItemType? = calendarItemTypeLogic.getCalendarItemType(calendarItemTypeId)

    override fun getCalendarItemTypes(calendarItemTypeIds: Collection<String>): Flow<CalendarItemType> = calendarItemTypeLogic.getCalendarItemTypes(calendarItemTypeIds)

    override fun getAllCalendarItemTypes(): Flow<CalendarItemType> = calendarItemTypeLogic.getAllCalendarItemTypes()

    override suspend fun modifyCalendarItemType(calendarItemType: CalendarItemType): CalendarItemType? = calendarItemTypeLogic.modifyCalendarTypeItem(calendarItemType)

    override fun getAllEntitiesIncludeDelete(): Flow<CalendarItemType> = calendarItemTypeLogic.getAllEntitiesIncludeDelete()
}