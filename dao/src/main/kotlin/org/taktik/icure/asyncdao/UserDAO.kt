/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import java.net.URI
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.results.BulkSaveResult
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.User
import org.taktik.icure.entities.security.Permission

interface UserDAO : GenericDAO<User> {
	fun listUserIdsByNameEmailPhone(datastoreInformation: IDatastoreInformation, searchString: String): Flow<String>
	fun listUsersByUsername(datastoreInformation: IDatastoreInformation, username: String): Flow<User>
	fun listUsersByEmail(datastoreInformation: IDatastoreInformation, searchString: String): Flow<User>
	fun listUsersByPhone(datastoreInformation: IDatastoreInformation, phone: String): Flow<User>
	fun findUsers(datastoreInformation: IDatastoreInformation, pagination: PaginationOffset<String>, skipPatients: Boolean = false): Flow<ViewQueryResultEvent>
	fun listUsersByHcpId(datastoreInformation: IDatastoreInformation, hcPartyId: String): Flow<User>
	fun listUsersByPatientId(datastoreInformation: IDatastoreInformation, patientId: String): Flow<User>
	suspend fun getUserOnUserDb(datastoreInformation: IDatastoreInformation, userId: String, bypassCache: Boolean): User
	suspend fun findUserOnUserDb(datastoreInformation: IDatastoreInformation, userId: String, bypassCache: Boolean): User?
	fun getUsersOnDb(datastoreInformation: IDatastoreInformation): Flow<User>
	suspend fun evictFromCache(datastoreInformation: IDatastoreInformation, userIds: Flow<String>)
	fun findUsersByIds(datastoreInformation: IDatastoreInformation, userIds: Flow<String>): Flow<ViewQueryResultEvent>
	fun findUsersByNameEmailPhone(datastoreInformation: IDatastoreInformation, searchString: String, pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>
}
