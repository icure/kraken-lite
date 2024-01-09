package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.TimeTableLogic
import org.taktik.icure.asyncservice.TimeTableService
import org.taktik.icure.entities.TimeTable
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult

@Service
class TimeTableServiceImpl(
    private val timeTableLogic: TimeTableLogic
) : TimeTableService {
    override suspend fun createTimeTable(timeTable: TimeTable): TimeTable? = timeTableLogic.createTimeTable(timeTable)

    override fun deleteTimeTables(ids: List<String>): Flow<DocIdentifier> = timeTableLogic.deleteTimeTables(ids)

    override suspend fun deleteTimeTable(timeTableId: String): DocIdentifier = timeTableLogic.deleteTimeTables(listOf(timeTableId)).single()

    override suspend fun getTimeTable(timeTableId: String): TimeTable? = timeTableLogic.getTimeTable(timeTableId)

    override fun getTimeTablesByPeriodAndAgendaId(startDate: Long, endDate: Long, agendaId: String): Flow<TimeTable> = timeTableLogic.getTimeTablesByPeriodAndAgendaId(startDate, endDate, agendaId)

    override fun getTimeTablesByAgendaId(agendaId: String): Flow<TimeTable> = timeTableLogic.getTimeTablesByAgendaId(agendaId)

    override suspend fun modifyTimeTable(timeTable: TimeTable): TimeTable = timeTableLogic.modifyEntities(listOf(timeTable)).single()

    override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<TimeTable>> = timeTableLogic.bulkShareOrUpdateMetadata(requests)
}
