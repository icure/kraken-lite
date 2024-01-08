/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.base.EntityWithSecureDelegationsLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.result.AggregatedAccessLogs
import org.taktik.icure.entities.AccessLog

interface AccessLogLogic : EntityWithSecureDelegationsLogic<AccessLog>, EntityPersister<AccessLog, String> {
	suspend fun createAccessLog(accessLog: AccessLog): AccessLog?
	fun deleteAccessLogs(ids: List<String>): Flow<DocIdentifier>
	fun listAccessLogsByHCPartyAndSecretPatientKeys(hcPartyId: String, secretForeignKeys: List<String>): Flow<AccessLog>
	suspend fun getAccessLog(accessLogId: String): AccessLog?
	fun listAccessLogsBy(fromEpoch: Long, toEpoch: Long, paginationOffset: PaginationOffset<Long>, descending: Boolean): Flow<ViewQueryResultEvent>
	fun findAccessLogsByUserAfterDate(userId: String, accessType: String?, startDate: Long?, pagination: PaginationOffset<List<*>>, descending: Boolean): Flow<ViewQueryResultEvent>
	suspend fun aggregatePatientByAccessLogs(userId: String, accessType: String?, startDate: Long?, startKey: String?, startDocumentId: String?, limit: Int): AggregatedAccessLogs
	fun deleteAccessLogs(ids: Flow<String>): Flow<DocIdentifier>
}
