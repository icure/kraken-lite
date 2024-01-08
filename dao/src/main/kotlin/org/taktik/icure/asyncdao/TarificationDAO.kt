/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Tarification

interface TarificationDAO : GenericDAO<Tarification> {
	fun listTarificationsBy(datastoreInformation: IDatastoreInformation, type: String?, code: String?, version: String?): Flow<Tarification>
	fun listTarificationsBy(datastoreInformation: IDatastoreInformation, region: String?, type: String?, code: String?, version: String?): Flow<Tarification>
	fun findTarificationsBy(datastoreInformation: IDatastoreInformation, region: String?, type: String?, code: String?, version: String?, pagination: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
	fun findTarificationsByLabel(datastoreInformation: IDatastoreInformation, region: String?, language: String?, label: String?, pagination: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
	fun findTarificationsByLabel(datastoreInformation: IDatastoreInformation, region: String?, language: String?, type: String?, label: String?, pagination: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
}
