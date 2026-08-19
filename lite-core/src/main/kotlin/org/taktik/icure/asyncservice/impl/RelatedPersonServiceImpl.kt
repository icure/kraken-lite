package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.RelatedPersonLogic
import org.taktik.icure.asyncservice.RelatedPersonService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.RelatedPerson
import org.taktik.icure.entities.conflicts.ConflictResolutionResult
import org.taktik.icure.entities.conflicts.ConflictResolutionStrategy
import org.taktik.icure.entities.conflicts.MergeResult
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult

@Service
class RelatedPersonServiceImpl(
	private val relatedPersonLogic: RelatedPersonLogic
) : RelatedPersonService {
	override suspend fun createRelatedPerson(relatedPerson: RelatedPerson): RelatedPerson =
		relatedPersonLogic.createEntity(relatedPerson)

	override fun createRelatedPersons(relatedPersons: List<RelatedPerson>): Flow<RelatedPerson> =
		relatedPersonLogic.createEntities(relatedPersons)

	override suspend fun getRelatedPerson(relatedPersonId: String): RelatedPerson? =
		relatedPersonLogic.getRelatedPerson(relatedPersonId)

	override fun getRelatedPersons(relatedPersonIds: Collection<String>): Flow<RelatedPerson> =
		relatedPersonLogic.getRelatedPersons(relatedPersonIds)

	override suspend fun modifyRelatedPerson(relatedPerson: RelatedPerson): RelatedPerson =
		relatedPersonLogic.modifyEntity(relatedPerson)

	override fun modifyRelatedPersons(relatedPersons: List<RelatedPerson>): Flow<RelatedPerson> =
		relatedPersonLogic.modifyEntities(relatedPersons)

	override fun deleteRelatedPersons(ids: List<IdAndRev>): Flow<RelatedPerson> =
		relatedPersonLogic.deleteEntities(ids)

	override suspend fun deleteRelatedPerson(
		id: String,
		rev: String?
	): RelatedPerson = relatedPersonLogic.deleteEntity(id, rev)

	override suspend fun purgeRelatedPerson(id: String, rev: String): DocIdentifier =
		relatedPersonLogic.purgeEntity(id, rev)

	override fun purgeRelatedPersons(relatedPersonIds: List<IdAndRev>): Flow<DocIdentifier> =
		relatedPersonLogic.purgeEntities(relatedPersonIds)

	override suspend fun undeleteRelatedPerson(
		id: String,
		rev: String
	): RelatedPerson = relatedPersonLogic.undeleteEntity(id, rev)

	override fun undeleteRelatedPersons(relatedPersonIds: List<IdAndRev>): Flow<RelatedPerson> =
		relatedPersonLogic.undeleteEntities(relatedPersonIds)

	override fun filterRelatedPersons(
		paginationOffset: PaginationOffset<Nothing>,
		filter: FilterChain<RelatedPerson>
	): Flow<ViewQueryResultEvent> = relatedPersonLogic.filter(paginationOffset, filter)

	override fun matchRelatedPersonsBy(filter: AbstractFilter<RelatedPerson>): Flow<String> =
		relatedPersonLogic.matchEntitiesBy(filter)

	override fun createEntities(entities: Flow<RelatedPerson>): Flow<RelatedPerson> =
		relatedPersonLogic.createEntities(entities)

	override fun modifyEntities(entities: Flow<RelatedPerson>): Flow<RelatedPerson> =
		relatedPersonLogic.modifyEntities(entities)

	override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<RelatedPerson>> =
		relatedPersonLogic.bulkShareOrUpdateMetadata(requests)

	override fun getConflictingEntitiesIds(): Flow<String> =
		relatedPersonLogic.getConflictingEntitiesIds()

	override fun getConflictsFor(entityId: String): Flow<RelatedPerson> =
		relatedPersonLogic.getConflictsFor(entityId)

	override suspend fun declareConflictWinner(
		entity: RelatedPerson,
		conflictsToPurge: List<String>
	): ConflictResolutionResult<RelatedPerson> {
		val conflicts = conflictsToPurge.mapNotNull { rev ->
			relatedPersonLogic.getBypassingCache(entity.id, rev)
		}
		return relatedPersonLogic.declareConflictWinner(entity, conflicts)
	}

	override fun solveConflicts(
		limit: Int?,
		ids: List<String>?,
		strategy: ConflictResolutionStrategy
	): Flow<MergeResult> = relatedPersonLogic.solveConflicts(limit, ids, strategy)
}