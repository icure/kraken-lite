package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.MedicalLocationLogic
import org.taktik.icure.asyncservice.MedicalLocationService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.MedicalLocation
import org.taktik.icure.pagination.PaginationElement

@Service
class MedicalLocationServiceImpl(
    private val medicalLocationLogic: MedicalLocationLogic
) : MedicalLocationService {
    override suspend fun createMedicalLocation(medicalLocation: MedicalLocation): MedicalLocation? = medicalLocationLogic.createMedicalLocation(medicalLocation)

    override fun deleteMedicalLocations(ids: List<String>): Flow<DocIdentifier> = medicalLocationLogic.deleteEntities(ids)

    override suspend fun getMedicalLocation(medicalLocation: String): MedicalLocation? = medicalLocationLogic.getEntity(medicalLocation)

    override suspend fun modifyMedicalLocation(medicalLocation: MedicalLocation): MedicalLocation? = medicalLocationLogic.modifyMedicalLocation(medicalLocation)

    override fun findMedicalLocationByPostCode(postCode: String): Flow<MedicalLocation> = medicalLocationLogic.findMedicalLocationByPostCode(postCode)
    override fun getAllMedicalLocations(paginationOffset: PaginationOffset<Nothing>): Flow<PaginationElement> = medicalLocationLogic.getAllMedicalLocations(paginationOffset)
    override fun getAllMedicalLocations(): Flow<MedicalLocation> = medicalLocationLogic.getEntities()
}
