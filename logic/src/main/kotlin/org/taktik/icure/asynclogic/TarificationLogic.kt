/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Tarification

interface TarificationLogic: EntityPersister<Tarification, String> {
	suspend fun getTarification(id: String): Tarification?
	suspend fun getTarification(type: String, tarification: String, version: String): Tarification?
	fun getTarifications(ids: List<String>): Flow<Tarification>
	suspend fun createTarification(tarification: Tarification): Tarification?
	suspend fun modifyTarification(tarification: Tarification): Tarification?
	fun findTarificationsBy(type: String?, tarification: String?, version: String?): Flow<Tarification>
	fun findTarificationsBy(region: String?, type: String?, tarification: String?, version: String?): Flow<Tarification>
	fun findTarificationsBy(region: String?, type: String?, tarification: String?, version: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
	fun findTarificationsByLabel(region: String?, language: String?, label: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
	fun findTarificationsByLabel(region: String?, language: String?, type: String?, label: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
	suspend fun getOrCreateTarification(type: String, tarification: String): Tarification?
}
