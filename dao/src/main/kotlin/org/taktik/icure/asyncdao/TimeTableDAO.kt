/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.entities.TimeTable

interface TimeTableDAO : GenericDAO<TimeTable> {
	fun listTimeTablesByAgendaId(datastoreInformation: IDatastoreInformation, agendaId: String): Flow<TimeTable>
	fun listTimeTablesByStartDateAndAgendaId(datastoreInformation: IDatastoreInformation, startDate: Long?, endDate: Long?, agendaId: String): Flow<TimeTable>
	fun listTimeTablesByEndDateAndAgendaId(datastoreInformation: IDatastoreInformation, startDate: Long?, endDate: Long?, agendaId: String): Flow<TimeTable>
	fun listTimeTablesByPeriodAndAgendaId(datastoreInformation: IDatastoreInformation, startDate: Long?, endDate: Long?, agendaId: String): Flow<TimeTable>
	fun listTimeTablesByAgendaIds(datastoreInformation: IDatastoreInformation, agendaIds: Collection<String>): Flow<TimeTable>
	fun listTimeTablesByPeriodAndAgendaIds(datastoreInformation: IDatastoreInformation, startDate: Long?, endDate: Long?, agendaIds: Collection<String>): Flow<TimeTable>
}
