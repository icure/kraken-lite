package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.icure.asynclogic.AccessLogLogic
import org.taktik.icure.asyncservice.AccessLogService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.result.AggregatedAccessLogs
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.pagination.PaginationElement

@Service
class AccessLogServiceImpl(
    private val accessLogLogic: AccessLogLogic
) : AccessLogService {
    override suspend fun createAccessLog(accessLog: AccessLog): AccessLog? = accessLogLogic.createAccessLog(accessLog)

    override fun deleteAccessLogs(ids: List<String>): Flow<DocIdentifier> = accessLogLogic.deleteAccessLogs(ids)

    override suspend fun deleteAccessLog(id: String): DocIdentifier = accessLogLogic.deleteAccessLogs(listOf(id)).single()

    override fun listAccessLogsByHCPartyAndSecretPatientKeys(
        hcPartyId: String,
        secretForeignKeys: List<String>
    ): Flow<AccessLog> = accessLogLogic.listAccessLogsByHCPartyAndSecretPatientKeys(hcPartyId, secretForeignKeys)

    override suspend fun getAccessLog(accessLogId: String): AccessLog? = accessLogLogic.getAccessLog(accessLogId)

    override fun listAccessLogsBy(
        fromEpoch: Long,
        toEpoch: Long,
        paginationOffset: PaginationOffset<Long>,
        descending: Boolean
    ): Flow<PaginationElement> = accessLogLogic.listAccessLogsBy(fromEpoch, toEpoch, paginationOffset, descending)

    override fun findAccessLogsByUserAfterDate(
        userId: String,
        accessType: String?,
        startDate: Long?,
        pagination: PaginationOffset<ComplexKey>,
        descending: Boolean
    ): Flow<PaginationElement> = accessLogLogic.findAccessLogsByUserAfterDate(userId, accessType, startDate, pagination, descending)

    override suspend fun modifyAccessLog(accessLog: AccessLog): AccessLog? = accessLogLogic.modifyEntities(listOf(accessLog)).singleOrNull()

    override fun getGenericLogic(): AccessLogLogic = accessLogLogic

    override suspend fun aggregatePatientByAccessLogs(
        userId: String,
        accessType: String?,
        startDate: Long?,
        startKey: String?,
        startDocumentId: String?,
        limit: Int
    ): AggregatedAccessLogs = accessLogLogic.aggregatePatientByAccessLogs(userId, accessType, startDate, startKey, startDocumentId, limit)

    override fun listAccessLogIdsByDataOwnerPatientDate(
        dataOwnerId: String,
        secretForeignKeys: Set<String>,
        startDate: Long?,
        endDate: Long?,
        descending: Boolean
    ): Flow<String> = accessLogLogic.listAccessLogIdsByDataOwnerPatientDate(dataOwnerId, secretForeignKeys, startDate, endDate, descending)


    override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<AccessLog>> = accessLogLogic.bulkShareOrUpdateMetadata(requests)
}