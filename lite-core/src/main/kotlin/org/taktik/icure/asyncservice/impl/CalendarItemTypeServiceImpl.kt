package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.CalendarItemTypeLogic
import org.taktik.icure.asyncservice.CalendarItemTypeService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.CalendarItemType
import org.taktik.icure.pagination.PaginationElement

@Service
class CalendarItemTypeServiceImpl(
    private val calendarItemTypeLogic: CalendarItemTypeLogic
) : CalendarItemTypeService {
    override suspend fun createCalendarItemType(calendarItemType: CalendarItemType): CalendarItemType? = calendarItemTypeLogic.createCalendarItemType(calendarItemType)
    override fun deleteCalendarItemTypes(ids: List<String>): Flow<CalendarItemType> = calendarItemTypeLogic.deleteEntities(ids.map { IdAndRev(it, null) })

    override suspend fun getCalendarItemType(calendarItemTypeId: String): CalendarItemType? = calendarItemTypeLogic.getCalendarItemType(calendarItemTypeId)

    override fun getCalendarItemTypes(calendarItemTypeIds: Collection<String>): Flow<CalendarItemType> = calendarItemTypeLogic.getCalendarItemTypes(calendarItemTypeIds)
    override fun getAllCalendarItemTypes(offset: PaginationOffset<Nothing>): Flow<PaginationElement> = calendarItemTypeLogic.getAllCalendarItemTypes(offset)
    override fun getAllCalendarItemTypes(): Flow<CalendarItemType> = calendarItemTypeLogic.getEntities()
    override suspend fun modifyCalendarItemType(calendarItemType: CalendarItemType): CalendarItemType? = calendarItemTypeLogic.modifyCalendarTypeItem(calendarItemType)
    override fun listCalendarItemTypesByAgendId(agendaId: String): Flow<CalendarItemType> = calendarItemTypeLogic.listCalendarItemTypesByAgendaId(agendaId)

    override fun getAllEntitiesIncludeDeleted(offset: PaginationOffset<String>): Flow<PaginationElement>  = calendarItemTypeLogic.getAllEntitiesIncludeDeleted(offset)
    override fun getAllEntitiesIncludeDeleted(): Flow<CalendarItemType> = calendarItemTypeLogic.getAllEntitiesIncludeDeleted()
}