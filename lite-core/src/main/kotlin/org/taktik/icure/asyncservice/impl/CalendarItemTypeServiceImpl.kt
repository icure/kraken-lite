package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.CalendarItemTypeLogic
import org.taktik.icure.asyncservice.CalendarItemTypeService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.CalendarItemType
import org.taktik.icure.entities.conflicts.ConflictResolutionResult
import org.taktik.icure.entities.conflicts.MergeResult
import org.taktik.icure.pagination.PaginationElement

@Service
class CalendarItemTypeServiceImpl(
	private val calendarItemTypeLogic: CalendarItemTypeLogic
) : CalendarItemTypeService {
	override suspend fun createCalendarItemType(calendarItemType: CalendarItemType): CalendarItemType = calendarItemTypeLogic.createCalendarItemType(calendarItemType)
	override fun createCalendarItemTypes(calendarItemTypes: List<CalendarItemType>): Flow<CalendarItemType> = calendarItemTypeLogic.createEntities(calendarItemTypes)

	override fun deleteCalendarItemTypes(ids: List<String>): Flow<CalendarItemType> = calendarItemTypeLogic.deleteEntities(ids.map { IdAndRev(it, null) })
	override fun deleteCalendarItemTypesWithRev(calendarItemTypeIds: List<IdAndRev>): Flow<DocIdentifier> = calendarItemTypeLogic.deleteEntities(calendarItemTypeIds).map { DocIdentifier(it.id, it.rev) }

	override suspend fun deleteCalendarItemType(
		id: String,
		rev: String
	): DocIdentifier = calendarItemTypeLogic.deleteEntity(id, rev).let { DocIdentifier(it.id, it.rev) }

	override fun undeleteCalendarItemTypes(calendarItemTypeIds: List<IdAndRev>): Flow<CalendarItemType> = calendarItemTypeLogic.undeleteEntities(calendarItemTypeIds)

	override suspend fun undeleteCalendarItemType(
		id: String,
		rev: String
	): CalendarItemType = calendarItemTypeLogic.undeleteEntity(id, rev)

	override suspend fun getCalendarItemType(calendarItemTypeId: String): CalendarItemType? = calendarItemTypeLogic.getCalendarItemType(calendarItemTypeId)

	override fun getCalendarItemTypes(calendarItemTypeIds: Collection<String>): Flow<CalendarItemType> = calendarItemTypeLogic.getCalendarItemTypes(calendarItemTypeIds)
	override suspend fun purgeCalendarItemType(
		id: String,
		rev: String
	): DocIdentifier = calendarItemTypeLogic.purgeEntity(id, rev)

	override fun purgeCalendarItemTypes(calendarItemTypeIds: List<IdAndRev>): Flow<DocIdentifier> = calendarItemTypeLogic.purgeEntities(calendarItemTypeIds)

	override fun getAllCalendarItemTypes(offset: PaginationOffset<Nothing>): Flow<PaginationElement> = calendarItemTypeLogic.getAllCalendarItemTypes(offset)
	override fun getAllCalendarItemTypes(): Flow<CalendarItemType> = calendarItemTypeLogic.getEntities()
	override suspend fun modifyCalendarItemType(calendarItemType: CalendarItemType): CalendarItemType = calendarItemTypeLogic.modifyCalendarTypeItem(calendarItemType)
	override fun modifyCalendarItemTypes(calendarItemTypes: List<CalendarItemType>): Flow<CalendarItemType> = calendarItemTypeLogic.modifyEntities(calendarItemTypes)

	override fun listCalendarItemTypesByAgendId(agendaId: String): Flow<CalendarItemType> = calendarItemTypeLogic.listCalendarItemTypesByAgendaId(agendaId)

	override fun getAllEntitiesIncludeDeleted(offset: PaginationOffset<String>): Flow<PaginationElement>  = calendarItemTypeLogic.getAllEntitiesIncludeDeleted(offset)
	override fun getAllEntitiesIncludeDeleted(): Flow<CalendarItemType> = calendarItemTypeLogic.getAllEntitiesIncludeDeleted()

	override fun getConflictingEntitiesIds(): Flow<String> = calendarItemTypeLogic.getConflictingEntitiesIds()
	override fun getConflictsFor(entityId: String): Flow<CalendarItemType> = calendarItemTypeLogic.getConflictsFor(entityId)
	override suspend fun declareConflictWinner(
		entity: CalendarItemType,
		conflictsToPurge: List<String>
	): ConflictResolutionResult<CalendarItemType> {
		val conflicts = conflictsToPurge.mapNotNull { rev ->
			calendarItemTypeLogic.getBypassingCache(entity.id, rev)
		}
		return calendarItemTypeLogic.declareConflictWinner(entity, conflicts)
	}
	override fun solveConflicts(
		limit: Int?,
		ids: List<String>?
	): Flow<MergeResult> = calendarItemTypeLogic.solveConflicts(limit, ids)
}