package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AgendaLogic
import org.taktik.icure.asyncservice.AgendaService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Agenda
import org.taktik.icure.pagination.PaginationElement

@Service
class AgendaServiceImpl(
    private val agendaLogic: AgendaLogic
) : AgendaService {
    override suspend fun createAgenda(agenda: Agenda): Agenda? = agendaLogic.createAgenda(agenda)

    override fun deleteAgendas(ids: Set<String>): Flow<DocIdentifier> = agendaLogic.deleteAgendas(ids)

    override suspend fun deleteAgenda(agendaId: String): DocIdentifier = agendaLogic.deleteAgendas(setOf(agendaId)).single()

    override suspend fun getAgenda(agendaId: String): Agenda? = agendaLogic.getAgenda(agendaId)

    override suspend fun modifyAgenda(agenda: Agenda): Agenda? = agendaLogic.modifyEntities(listOf(agenda)).singleOrNull()

    override fun getAgendasByUser(userId: String): Flow<Agenda> = agendaLogic.getAgendasByUser(userId)

    override fun getReadableAgendaForUser(userId: String): Flow<Agenda> = agendaLogic.getReadableAgendaForUser(userId)
    override fun getAllAgendas(offset: PaginationOffset<Nothing>): Flow<PaginationElement> = agendaLogic.getAllPaginated(offset)
    override fun getAllAgendas(): Flow<Agenda>  = agendaLogic.getEntities()

}