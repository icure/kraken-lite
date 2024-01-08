/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncservice

import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.AccessDeniedException
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.AccessLogLogic
import org.taktik.icure.asyncservice.base.EntityWithSecureDelegationsService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.result.AggregatedAccessLogs
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.exceptions.NotFoundRequestException

interface AccessLogService : EntityWithSecureDelegationsService<AccessLog> {
	suspend fun createAccessLog(accessLog: AccessLog): AccessLog?

	/**
	 * Deletes a batch of [AccessLog]s.
	 * If the user does not have the permission to delete an [AccessLog] or the [AccessLog] does not exist, then it
	 * is ignored without any warning.
	 *
	 * @param ids a [List] containing the ids of the [AccessLog]s to delete.
	 * @return a [Flow] containing the [DocIdentifier]s of the [AccessLog]s successfully deleted.
	 */
	fun deleteAccessLogs(ids: List<String>): Flow<DocIdentifier>

	/**
	 * Deletes an [AccessLog].
	 *
	 * @param id the id of the [AccessLog] to delete.
	 * @return a [DocIdentifier] related to the [AccessLog] if the operation completes successfully.
	 * @throws [AccessDeniedException] if the current user does not have the permission to delete the [AccessLog].
	 * @throws [NotFoundRequestException] if an [AccessLog] with the specified [id] does not exist.
	 */
	suspend fun deleteAccessLog(id: String): DocIdentifier
	fun listAccessLogsByHCPartyAndSecretPatientKeys(hcPartyId: String, secretForeignKeys: List<String>): Flow<AccessLog>
	suspend fun getAccessLog(accessLogId: String): AccessLog?
	fun listAccessLogsBy(fromEpoch: Long, toEpoch: Long, paginationOffset: PaginationOffset<Long>, descending: Boolean): Flow<ViewQueryResultEvent>
	fun findAccessLogsByUserAfterDate(userId: String, accessType: String?, startDate: Long?, pagination: PaginationOffset<List<*>>, descending: Boolean): Flow<ViewQueryResultEvent>
	suspend fun modifyAccessLog(accessLog: AccessLog): AccessLog?
	fun getGenericLogic(): AccessLogLogic
	suspend fun aggregatePatientByAccessLogs(userId: String, accessType: String?, startDate: Long?, startKey: String?, startDocumentId: String?, limit: Int): AggregatedAccessLogs
}
