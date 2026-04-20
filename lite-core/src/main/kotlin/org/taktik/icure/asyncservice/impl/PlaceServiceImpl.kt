package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.PlaceLogic
import org.taktik.icure.asyncservice.PlaceService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Place
import org.taktik.icure.entities.conflicts.ConflictResolutionResult
import org.taktik.icure.entities.conflicts.MergeResult
import org.taktik.icure.pagination.PaginationElement

@Service
class PlaceServiceImpl(
	private val placeLogic: PlaceLogic
) : PlaceService {
	override fun getAllPlaces(paginationOffset: PaginationOffset<Nothing>): Flow<PaginationElement> = placeLogic.getAllPlaces(paginationOffset)
	override fun getAllPlaces(): Flow<Place> = placeLogic.getEntities()
	override suspend fun createPlace(place: Place): Place = placeLogic.createPlace(place)
	override fun createPlaces(places: List<Place>): Flow<Place> = placeLogic.createEntities(places)
	override suspend fun getPlace(place: String): Place? = placeLogic.getPlace(place)
	override fun getPlaces(placeIds: List<String>): Flow<Place> = placeLogic.getEntities(placeIds)
	override suspend fun modifyPlace(place: Place): Place = placeLogic.modifyPlace(place)
	override fun modifyPlaces(places: List<Place>): Flow<Place> = placeLogic.modifyEntities(places)
	override suspend fun deletePlace(placeId: String, rev: String): DocIdentifier = placeLogic.deleteEntity(placeId, rev).let { DocIdentifier(it.id, it.rev) }
	override fun deletePlaces(placeIds: List<IdAndRev>): Flow<Place> = placeLogic.deleteEntities(placeIds)
	override suspend fun undeletePlace(placeId: String, rev: String): Place = placeLogic.undeleteEntity(placeId, rev)
	override fun undeletePlaces(placeIds: List<IdAndRev>): Flow<Place> = placeLogic.undeleteEntities(placeIds)
	override suspend fun purgePlace(placeId: String, rev: String): DocIdentifier = placeLogic.purgeEntity(placeId, rev).let { DocIdentifier(it.id, it.rev) }
	override fun purgePlaces(placeIds: List<IdAndRev>): Flow<DocIdentifier> = placeLogic.purgeEntities(placeIds)
	override fun getConflictingEntitiesIds(): Flow<String> = placeLogic.getConflictingEntitiesIds()
	override fun getConflictsFor(entityId: String): Flow<Place> = placeLogic.getConflictsFor(entityId)
	override suspend fun declareConflictWinner(
		entity: Place,
		conflictsToPurge: List<String>
	): ConflictResolutionResult<Place> {
		val conflicts = conflictsToPurge.mapNotNull { rev ->
			placeLogic.getBypassingCache(entity.id, rev)
		}
		return placeLogic.declareConflictWinner(entity, conflicts)
	}
	override fun solveConflicts(
		limit: Int?,
		ids: List<String>?
	): Flow<MergeResult> = placeLogic.solveConflicts(limit, ids)
}
