package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.singleOrNull
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.AgendaLogic
import org.taktik.icure.asyncservice.AgendaService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.Agenda
import org.taktik.icure.pagination.PaginationElement

@Service
class AgendaServiceImpl(
    private val agendaLogic: AgendaLogic
) : AgendaService {
    override suspend fun createAgenda(agenda: Agenda): Agenda? = agendaLogic.createAgenda(agenda)
    override fun deleteAgendas(ids: List<IdAndRev>): Flow<Agenda> = agendaLogic.deleteEntities(ids)
    override suspend fun deleteAgenda(id: String, rev: String?): Agenda = agendaLogic.deleteEntity(id, rev)
    override suspend fun purgeAgenda(id: String, rev: String): DocIdentifier = agendaLogic.purgeEntity(id, rev)
    override suspend fun undeleteAgenda(id: String, rev: String): Agenda = agendaLogic.undeleteEntity(id, rev)

    override suspend fun getAgenda(agendaId: String): Agenda? = agendaLogic.getAgenda(agendaId)
    override fun getAgendas(agendaIds: List<String>): Flow<Agenda> = agendaLogic.getEntities(agendaIds)

    override suspend fun modifyAgenda(agenda: Agenda): Agenda? = agendaLogic.modifyEntities(listOf(agenda)).singleOrNull()

    override fun getAgendasByUser(userId: String): Flow<Agenda> = agendaLogic.getAgendasByUser(userId)

    @Deprecated("Based on legacy Agenda.rights ; use filter for agendas using userRights")
    override fun getReadableAgendaForUser(userId: String): Flow<Agenda> = agendaLogic.getReadableAgendaForUserLegacy(userId)
    override fun getAllAgendas(offset: PaginationOffset<Nothing>): Flow<PaginationElement> = agendaLogic.getAllPaginated(offset)
    override fun getAllAgendas(): Flow<Agenda>  = agendaLogic.getEntities()
    override fun matchAgendasBy(filter: AbstractFilter<Agenda>): Flow<String> = agendaLogic.matchEntitiesBy(filter)

}