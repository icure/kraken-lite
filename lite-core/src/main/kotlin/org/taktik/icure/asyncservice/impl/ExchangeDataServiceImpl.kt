package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.ExchangeDataLogic
import org.taktik.icure.asyncservice.ExchangeDataService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.DataOwnerType
import org.taktik.icure.entities.ExchangeData

@Service
class ExchangeDataServiceImpl(
    private val exchangeDataLogic: ExchangeDataLogic
) : ExchangeDataService {
    override suspend fun getExchangeDataById(id: String): ExchangeData? = exchangeDataLogic.getExchangeDataById(id)

    override fun findExchangeDataByParticipant(
        dataOwnerId: String,
        paginationOffset: PaginationOffset<String>
    ): Flow<ViewQueryResultEvent> = exchangeDataLogic.findExchangeDataByParticipant(dataOwnerId, paginationOffset)

    override fun findExchangeDataByDelegatorDelegatePair(delegatorId: String, delegateId: String): Flow<ExchangeData> =
        exchangeDataLogic.findExchangeDataByDelegatorDelegatePair(delegatorId, delegateId)

    override suspend fun createExchangeData(exchangeData: ExchangeData): ExchangeData =
        exchangeDataLogic.createExchangeData(exchangeData)

    override suspend fun modifyExchangeData(exchangeData: ExchangeData): ExchangeData =
        exchangeDataLogic.modifyExchangeData(exchangeData)

    override fun getParticipantCounterparts(dataOwnerId: String, counterpartsType: List<DataOwnerType>): Flow<String> =
        exchangeDataLogic.getParticipantCounterparts(dataOwnerId, counterpartsType)
}