package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.icure.asynclogic.TarificationLogic
import org.taktik.icure.asyncservice.PricingService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.Tarification
import org.taktik.icure.pagination.PaginationElement

@Service
class PricingServiceImpl(
    private val tarificationLogic: TarificationLogic
) : PricingService {
    override suspend fun getTarification(id: String): Tarification? = tarificationLogic.getTarification(id)

    override suspend fun getTarification(type: String, tarification: String, version: String): Tarification? = tarificationLogic.getTarification(type, tarification, version)

    override fun getTarifications(ids: List<String>): Flow<Tarification> = tarificationLogic.getTarifications(ids)

    override suspend fun createTarification(tarification: Tarification): Tarification? = tarificationLogic.createTarification(tarification)

    override suspend fun modifyTarification(tarification: Tarification): Tarification? = tarificationLogic.modifyTarification(tarification)

    override fun findTarificationsBy(type: String?, tarification: String?, version: String?): Flow<Tarification> = tarificationLogic.findTarificationsBy(type, tarification, version)

    override fun findTarificationsBy(
        region: String?,
        type: String?,
        tarification: String?,
        version: String?
    ): Flow<Tarification> = tarificationLogic.findTarificationsBy(region, type, tarification, version)

    override fun matchTarificationsBy(filter: AbstractFilter<Tarification>): Flow<String> {
        throw NotImplementedError()
    }

    override fun findTarificationsBy(
        region: String?,
        type: String?,
        tarification: String?,
        version: String?,
        paginationOffset: PaginationOffset<ComplexKey>
    ): Flow<PaginationElement> = tarificationLogic.findTarificationsBy(region, type, tarification, version, paginationOffset)

    override fun findTarificationsOfTypesByLabel(
        region: String?,
        language: String?,
        label: String?,
        types: Set<String>?,
        paginationOffset: PaginationOffset<ComplexKey>
    ): Flow<PaginationElement> = tarificationLogic.findTarificationsOfTypesByLabel(region, language, label, types, paginationOffset)

    override fun findTarificationsByLabel(
        region: String?,
        language: String?,
        type: String?,
        label: String?,
        paginationOffset: PaginationOffset<List<String?>>
    ): Flow<ViewQueryResultEvent> = tarificationLogic.findTarificationsByLabel(region, language, type, label, paginationOffset)

    override suspend fun getOrCreateTarification(type: String, tarification: String): Tarification? = tarificationLogic.getOrCreateTarification(type, tarification)
}
