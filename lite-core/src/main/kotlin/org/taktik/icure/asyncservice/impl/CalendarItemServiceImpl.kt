package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.CalendarItemLogic
import org.taktik.icure.asyncservice.CalendarItemService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.CalendarItem
import org.taktik.icure.entities.conflicts.ConflictResolutionResult
import org.taktik.icure.entities.conflicts.MergeResult
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.pagination.PaginationElement

@Service
class CalendarItemServiceImpl(
	private val calendarItemLogic: CalendarItemLogic
) : CalendarItemService {
	override suspend fun createCalendarItem(calendarItem: CalendarItem): CalendarItem = calendarItemLogic.createCalendarItem(calendarItem)
	override fun createCalendarItems(calendarItems: List<CalendarItem>): Flow<CalendarItem>  = calendarItemLogic.createCalendarItems(calendarItems)

	override fun deleteCalendarItems(ids: List<IdAndRev>): Flow<CalendarItem> = calendarItemLogic.deleteEntities(ids)
	override suspend fun deleteCalendarItem(id: String, rev: String?): CalendarItem = calendarItemLogic.deleteEntity(id, rev)
	override suspend fun purgeCalendarItem(id: String, rev: String): DocIdentifier = calendarItemLogic.purgeEntity(id, rev)
	override fun purgeCalendarItems(ids: List<IdAndRev>): Flow<DocIdentifier> = calendarItemLogic.purgeEntities(ids)

	override suspend fun undeleteCalendarItem(id: String, rev: String): CalendarItem = calendarItemLogic.undeleteEntity(id, rev)
	override fun undeleteCalendarItems(ids: List<IdAndRev>): Flow<CalendarItem> = calendarItemLogic.undeleteEntities(ids)

	override suspend fun getCalendarItem(calendarItemId: String): CalendarItem? = calendarItemLogic.getCalendarItem(calendarItemId)

	override fun getCalendarItemByPeriodAndHcPartyId(
		startDate: Long,
		endDate: Long,
		hcPartyId: String
	): Flow<CalendarItem> = calendarItemLogic.getCalendarItemByPeriodAndHcPartyId(startDate, endDate, hcPartyId)

	override fun getCalendarItemByPeriodAndAgendaId(
		startDate: Long,
		endDate: Long,
		agendaId: String
	): Flow<CalendarItem> = calendarItemLogic.getCalendarItemByPeriodAndAgendaId(startDate, endDate, agendaId, false)

	override fun findCalendarItemIdsByDataOwnerPatientStartTime(
		dataOwnerId: String,
		secretForeignKeys: Set<String>,
		startDate: Long?,
		endDate: Long?,
		descending: Boolean
	): Flow<String> = calendarItemLogic.findCalendarItemIdsByDataOwnerPatientStartTime(dataOwnerId, secretForeignKeys, startDate, endDate, descending)

	override fun listCalendarItemsByHCPartyAndSecretPatientKeys(
		hcPartyId: String,
		secretPatientKeys: List<String>
	): Flow<CalendarItem> = calendarItemLogic.listCalendarItemsByHCPartyAndSecretPatientKeys(hcPartyId, secretPatientKeys)

	override suspend fun modifyCalendarItem(calendarItem: CalendarItem): CalendarItem = calendarItemLogic.modifyEntity(calendarItem)
	override fun getAllCalendarItems(offset: PaginationOffset<Nothing>): Flow<PaginationElement> = calendarItemLogic.getAllCalendarItems(offset)
	override fun getAllCalendarItems(): Flow<CalendarItem> = calendarItemLogic.getEntities()
	override fun getCalendarItems(ids: List<String>): Flow<CalendarItem> = calendarItemLogic.getCalendarItems(ids)
	override fun getCalendarItemsByRecurrenceId(
		recurrenceId: String,
		paginationOffset: PaginationOffset<String>
	): Flow<PaginationElement> = calendarItemLogic.getCalendarItemsByRecurrenceId(recurrenceId, paginationOffset)

	override fun getCalendarItemsByRecurrenceId(recurrenceId: String): Flow<CalendarItem> = calendarItemLogic.getCalendarItemsByRecurrenceId(recurrenceId)
	override fun modifyCalendarItems(entities: Collection<CalendarItem>): Flow<CalendarItem> = calendarItemLogic.modifyEntities(entities)

	override fun findCalendarItemsByHCPartyAndSecretPatientKeys(
		hcPartyId: String,
		secretPatientKeys: List<String>,
		paginationOffset: PaginationOffset<List<Any>>
	): Flow<ViewQueryResultEvent> = calendarItemLogic.findCalendarItemsByHCPartyAndSecretPatientKeys(hcPartyId, secretPatientKeys, paginationOffset)

	override fun matchCalendarItemsBy(filter: AbstractFilter<CalendarItem>): Flow<String> = calendarItemLogic.matchEntitiesBy(filter)

	override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<CalendarItem>> = calendarItemLogic.bulkShareOrUpdateMetadata(requests)

	override fun getConflictingEntitiesIds(): Flow<String> = calendarItemLogic.getConflictingEntitiesIds()
	override fun getConflictsFor(entityId: String): Flow<CalendarItem> = calendarItemLogic.getConflictsFor(entityId)
	override suspend fun declareConflictWinner(
		entity: CalendarItem,
		conflictsToPurge: List<String>
	): ConflictResolutionResult<CalendarItem> {
		val conflicts = conflictsToPurge.mapNotNull { rev ->
			calendarItemLogic.getBypassingCache(entity.id, rev)
		}
		return calendarItemLogic.declareConflictWinner(entity, conflicts)
	}
	override fun solveConflicts(
		limit: Int?,
		ids: List<String>?
	): Flow<MergeResult> = calendarItemLogic.solveConflicts(limit, ids)
}