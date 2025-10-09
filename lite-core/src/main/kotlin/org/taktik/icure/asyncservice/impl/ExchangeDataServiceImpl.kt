package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.ExchangeDataLogic
import org.taktik.icure.asyncservice.ExchangeDataService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.DataOwnerType
import org.taktik.icure.entities.ExchangeData
import org.taktik.icure.pagination.PaginationElement

@Service
class ExchangeDataServiceImpl(
    private val exchangeDataLogic: ExchangeDataLogic
) : ExchangeDataService {
    override suspend fun getExchangeDataById(id: String): ExchangeData? = exchangeDataLogic.getExchangeDataById(id)
    override fun getExchangeDataByIds(ids: List<String>): Flow<ExchangeData> = exchangeDataLogic.getExchangeDataByIds(ids)

    override fun findExchangeDataByParticipant(
        dataOwnerId: String,
        paginationOffset: PaginationOffset<String>
    ): Flow<PaginationElement> = exchangeDataLogic.findExchangeDataByParticipant(dataOwnerId, paginationOffset)

    override fun findExchangeDataByDelegatorDelegatePair(delegatorId: String, delegateId: String): Flow<ExchangeData> =
        exchangeDataLogic.findExchangeDataByDelegatorDelegatePair(delegatorId, delegateId)

    override suspend fun createExchangeData(exchangeData: ExchangeData): ExchangeData =
        exchangeDataLogic.createExchangeData(exchangeData)

    override suspend fun createExchangeDatas(exchangeDatas: List<ExchangeData>): Flow<ExchangeData> =
        exchangeDataLogic.createExchangeDatas(exchangeDatas)

    override suspend fun modifyExchangeData(exchangeData: ExchangeData): ExchangeData =
        exchangeDataLogic.modifyExchangeData(exchangeData)

    override fun getParticipantCounterparts(
        dataOwnerId: String,
        counterpartsType: List<DataOwnerType>,
        ignoreOnEntryForFingerprint: String?
    ): Flow<String> = exchangeDataLogic.getParticipantCounterparts(dataOwnerId, counterpartsType, ignoreOnEntryForFingerprint)
}
