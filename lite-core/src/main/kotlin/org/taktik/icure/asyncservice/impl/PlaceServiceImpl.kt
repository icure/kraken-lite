package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.PlaceLogic
import org.taktik.icure.asyncservice.PlaceService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Place
import org.taktik.icure.pagination.PaginationElement

@Service
class PlaceServiceImpl(
    private val placeLogic: PlaceLogic
) : PlaceService {
    override fun getAllPlaces(paginationOffset: PaginationOffset<Nothing>): Flow<PaginationElement> = placeLogic.getAllPlaces(paginationOffset)
    override fun getAllPlaces(): Flow<Place> = placeLogic.getEntities()
    override suspend fun createPlace(place: Place): Place? = placeLogic.createPlace(place)
    override suspend fun deletePlace(id: String, rev: String?): Place = placeLogic.deleteEntity(id, rev)
    override suspend fun getPlace(place: String): Place? = placeLogic.getPlace(place)

    override suspend fun modifyPlace(place: Place): Place? = placeLogic.modifyPlace(place)
    override fun deletePlaces(identifiers: List<IdAndRev>): Flow<Place> = placeLogic.deleteEntities(identifiers)
}
