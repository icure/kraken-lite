package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.PlaceLogic
import org.taktik.icure.asyncservice.PlaceService
import org.taktik.icure.entities.Place

@Service
class PlaceServiceImpl(
    private val placeLogic: PlaceLogic
) : PlaceService {
    override fun getAllPlaces(): Flow<Place> = placeLogic.getEntities()

    override suspend fun createPlace(place: Place): Place? = placeLogic.createPlace(place)

    override fun deletePlace(ids: List<String>): Flow<DocIdentifier> = placeLogic.deleteEntities(ids)

    override suspend fun getPlace(place: String): Place? = placeLogic.getPlace(place)

    override suspend fun modifyPlace(place: Place): Place? = placeLogic.modifyPlace(place)

    override fun deletePlaces(identifiers: Set<String>): Flow<DocIdentifier> = placeLogic.deletePlace(identifiers.toList())
}
