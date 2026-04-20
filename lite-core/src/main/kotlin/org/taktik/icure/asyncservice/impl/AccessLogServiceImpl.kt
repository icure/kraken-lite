package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.AccessLogLogic
import org.taktik.icure.asyncservice.AccessLogService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.result.AggregatedAccessLogs
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.entities.conflicts.ConflictResolutionResult
import org.taktik.icure.entities.conflicts.MergeResult
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.pagination.PaginationElement

@Service
class AccessLogServiceImpl(
	private val accessLogLogic: AccessLogLogic
) : AccessLogService {
	override suspend fun createAccessLog(accessLog: AccessLog): AccessLog = accessLogLogic.createAccessLog(accessLog)
	override fun createAccessLogs(accessLog: List<AccessLog>): Flow<AccessLog> = accessLogLogic.createAccessLogs(accessLog)
	override fun deleteAccessLogs(ids: List<IdAndRev>): Flow<AccessLog> = accessLogLogic.deleteEntities(ids)
	override suspend fun deleteAccessLog(id: String, rev: String?): AccessLog = accessLogLogic.deleteEntity(id, rev)
	override suspend fun purgeAccessLog(id: String, rev: String): DocIdentifier = accessLogLogic.purgeEntity(id, rev)
	override fun purgeAccessLogs(ids: List<IdAndRev>): Flow<DocIdentifier> = accessLogLogic.purgeEntities(ids)

	override suspend fun undeleteAccessLog(id: String, rev: String): AccessLog = accessLogLogic.undeleteEntity(id, rev)
	override fun undeleteAccessLogs(ids: List<IdAndRev>): Flow<AccessLog> = accessLogLogic.undeleteEntities(ids)

	@Suppress("DEPRECATION")
	@Deprecated("This method cannot include results with secure delegations, use listAccessLogIdsByDataOwnerPatientDate instead")
	override fun listAccessLogsByHCPartyAndSecretPatientKeys(
		hcPartyId: String,
		secretForeignKeys: List<String>
	): Flow<AccessLog> = accessLogLogic.listAccessLogsByHCPartyAndSecretPatientKeys(hcPartyId, secretForeignKeys)

	override suspend fun getAccessLog(accessLogId: String): AccessLog? = accessLogLogic.getAccessLog(accessLogId)
	override fun getAccessLogs(ids: List<String>): Flow<AccessLog> = accessLogLogic.getAccessLogs(ids)

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

	override suspend fun modifyAccessLog(accessLog: AccessLog): AccessLog = accessLogLogic.modifyEntity(accessLog)
	override fun modifyAccessLogs(accessLogs: List<AccessLog>): Flow<AccessLog> = accessLogLogic.modifyEntities(accessLogs)

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

	override fun matchAccessLogsBy(filter: AbstractFilter<AccessLog>): Flow<String> = accessLogLogic.matchEntitiesBy(filter)
	override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<AccessLog>> = accessLogLogic.bulkShareOrUpdateMetadata(requests)
	override fun getConflictingEntitiesIds(): Flow<String> = accessLogLogic.getConflictingEntitiesIds()
	override fun getConflictsFor(entityId: String): Flow<AccessLog> = accessLogLogic.getConflictsFor(entityId)
	override suspend fun declareConflictWinner(
		entity: AccessLog,
		conflictsToPurge: List<String>
	): ConflictResolutionResult<AccessLog> {
		val conflicts = conflictsToPurge.mapNotNull { rev ->
			accessLogLogic.getBypassingCache(entity.id, rev)
		}
		return accessLogLogic.declareConflictWinner(entity, conflicts)
	}
	override fun solveConflicts(
		limit: Int?,
		ids: List<String>?
	): Flow<MergeResult> = accessLogLogic.solveConflicts(limit, ids)
}