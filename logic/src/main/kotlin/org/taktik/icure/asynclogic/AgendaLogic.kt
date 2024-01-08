/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.entities.Agenda

interface AgendaLogic : EntityPersister<Agenda, String> {
	suspend fun createAgenda(agenda: Agenda): Agenda?
	fun deleteAgendas(ids: Set<String>): Flow<DocIdentifier>

	suspend fun getAgenda(agenda: String): Agenda?
	suspend fun modifyAgenda(agenda: Agenda): Agenda?
	fun getAgendasByUser(userId: String): Flow<Agenda>
	fun getReadableAgendaForUser(userId: String): Flow<Agenda>
}
