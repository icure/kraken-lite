package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.InsuranceLogic
import org.taktik.icure.asyncservice.InsuranceService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Insurance
import org.taktik.icure.entities.conflicts.ConflictResolutionResult
import org.taktik.icure.entities.conflicts.MergeResult
import org.taktik.icure.pagination.PaginationElement

@Service
class InsuranceServiceImpl(
	private val insuranceLogic: InsuranceLogic
) : InsuranceService {
	override suspend fun createInsurance(insurance: Insurance): Insurance = insuranceLogic.createInsurance(insurance)
	override fun createInsurances(insurances: List<Insurance>): Flow<Insurance> = insuranceLogic.createEntities(insurances)
	override suspend fun deleteInsurance(insuranceId: String, rev: String?): Insurance = insuranceLogic.deleteEntity(insuranceId, rev)
	override fun deleteInsurances(insuranceIds: List<IdAndRev>): Flow<DocIdentifier> = deleteInsurances(insuranceIds)
	override suspend fun undeleteInsurance(
		insuranceId: String,
		rev: String
	): Insurance = insuranceLogic.undeleteEntity(insuranceId, rev)

	override fun undeleteInsurances(insuranceIds: List<IdAndRev>): Flow<Insurance> = insuranceLogic.undeleteEntities(insuranceIds)

	override suspend fun purgeInsurance(
		insuranceId: String,
		rev: String
	): DocIdentifier = insuranceLogic.purgeEntity(insuranceId, rev)

	override fun purgeInsurances(insuranceIds: List<IdAndRev>): Flow<DocIdentifier> = insuranceLogic.purgeEntities(insuranceIds)

	override suspend fun getInsurance(insuranceId: String): Insurance? = insuranceLogic.getInsurance(insuranceId)

	override fun listInsurancesByCode(code: String): Flow<Insurance> = insuranceLogic.listInsurancesByCode(code)

	override fun listInsurancesByName(name: String): Flow<Insurance> = insuranceLogic.listInsurancesByName(name)

	override suspend fun modifyInsurance(insurance: Insurance): Insurance = insuranceLogic.modifyInsurance(insurance)
	override fun modifyInsurances(insurances: List<Insurance>): Flow<Insurance> = insuranceLogic.modifyEntities(insurances)

	override fun getInsurances(ids: Set<String>): Flow<Insurance> = insuranceLogic.getInsurances(ids)

	override fun getAllInsurances(paginationOffset: PaginationOffset<Nothing>): Flow<PaginationElement> = insuranceLogic.getAllInsurances(paginationOffset)

	override fun getConflictingEntitiesIds(): Flow<String> = insuranceLogic.getConflictingEntitiesIds()
	override fun getConflictsFor(entityId: String): Flow<Insurance> = insuranceLogic.getConflictsFor(entityId)
	override suspend fun declareConflictWinner(
		entity: Insurance,
		conflictsToPurge: List<String>
	): ConflictResolutionResult<Insurance> {
		val conflicts = conflictsToPurge.mapNotNull { rev ->
			insuranceLogic.getBypassingCache(entity.id, rev)
		}
		return insuranceLogic.declareConflictWinner(entity, conflicts)
	}
	override fun solveConflicts(
		limit: Int?,
		ids: List<String>?
	): Flow<MergeResult> = insuranceLogic.solveConflicts(limit, ids)
}
