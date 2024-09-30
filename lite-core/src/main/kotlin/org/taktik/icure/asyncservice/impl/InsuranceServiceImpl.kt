package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.InsuranceLogic
import org.taktik.icure.asyncservice.InsuranceService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Insurance
import org.taktik.icure.pagination.PaginationElement

@Service
class InsuranceServiceImpl(
    private val insuranceLogic: InsuranceLogic
) : InsuranceService {
    override suspend fun createInsurance(insurance: Insurance): Insurance? = insuranceLogic.createInsurance(insurance)
    override suspend fun deleteInsurance(insuranceId: String, rev: String?): DocIdentifier = insuranceLogic.deleteEntity(insuranceId, rev)

    override suspend fun getInsurance(insuranceId: String): Insurance? = insuranceLogic.getInsurance(insuranceId)

    override fun listInsurancesByCode(code: String): Flow<Insurance> = insuranceLogic.listInsurancesByCode(code)

    override fun listInsurancesByName(name: String): Flow<Insurance> = insuranceLogic.listInsurancesByName(name)

    override suspend fun modifyInsurance(insurance: Insurance): Insurance? = insuranceLogic.modifyInsurance(insurance)

    override fun getInsurances(ids: Set<String>): Flow<Insurance> = insuranceLogic.getInsurances(ids)

    override fun getAllInsurances(paginationOffset: PaginationOffset<Nothing>): Flow<PaginationElement> = insuranceLogic.getAllInsurances(paginationOffset)
}
