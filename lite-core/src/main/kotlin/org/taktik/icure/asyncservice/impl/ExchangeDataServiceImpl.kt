package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.ExchangeDataLogic
import org.taktik.icure.asyncservice.ExchangeDataService
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.DataOwnerType
import org.taktik.icure.entities.ExchangeData
import org.taktik.icure.entities.requests.BulkExchangeDataPieceCreationRequest
import org.taktik.icure.entities.requests.ExchangeDataPieceCreationRequest
import org.taktik.icure.pagination.MultiKeyPaginationElement
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

    override fun modifyExchangeDatas(exchangeDatas: List<ExchangeData>): Flow<ExchangeData> =
        exchangeDataLogic.modifyExchangeDatas(exchangeDatas)

    override fun findNonGroupPieceCounterparts(
        dataOwnerId: String,
        counterpartsTypes: List<DataOwnerType>,
        ignoreOnEntryForFingerprint: String?,
        startCounterpartId: String?,
        limit: Int?,
    ): Flow<PaginationElement> = exchangeDataLogic.findNonGroupPieceCounterparts(
        dataOwnerId, counterpartsTypes, ignoreOnEntryForFingerprint, startCounterpartId, limit
    )

    override fun findExchangeDataGroupByIdForRecipients(
        exchangeDataOrGroupId: String,
        filterRecipients: List<String?>,
        startDocumentId: String?,
    ): Flow<MultiKeyPaginationElement<ExchangeData, String?>> =
        exchangeDataLogic.findExchangeDataGroupByIdForRecipients(exchangeDataOrGroupId, filterRecipients, startDocumentId)

    override fun findExchangeDataByParticipantForRecipients(
        dataOwnerId: String,
        filterRecipients: List<String?>,
        startDocumentId: String?,
    ): Flow<MultiKeyPaginationElement<ExchangeData, String?>> =
        exchangeDataLogic.findExchangeDataByParticipantForRecipients(dataOwnerId, filterRecipients, startDocumentId)

    override fun findExchangeDataByDelegatorDelegateForRecipients(
        delegatorId: String,
        delegateId: String,
        filterRecipients: List<String?>,
        startDocumentId: String?,
    ): Flow<MultiKeyPaginationElement<ExchangeData, String?>> =
        exchangeDataLogic.findExchangeDataByDelegatorDelegateForRecipients(delegatorId, delegateId, filterRecipients, startDocumentId)

    override fun findExchangeDataGroupById(
        exchangeDataOrGroupId: String,
        paginationOffset: PaginationOffset<ComplexKey>,
    ): Flow<PaginationElement> = exchangeDataLogic.findExchangeDataGroupById(exchangeDataOrGroupId, paginationOffset)

    override fun createExchangeDataGroupPieces(
        exchangeDataGroupId: String,
        delegator: String,
        delegate: String,
        piecesByRecipient: Map<String, ExchangeDataPieceCreationRequest>,
    ): Flow<ExchangeData> =
        exchangeDataLogic.createExchangeDataGroupPieces(exchangeDataGroupId, delegator, delegate, piecesByRecipient)

    override fun bulkCreateExchangeDataGroupPieces(
        requests: List<BulkExchangeDataPieceCreationRequest>,
    ): Flow<ExchangeData> = exchangeDataLogic.bulkCreateExchangeDataGroupPieces(requests)

    override fun findMainExchangeDataIdsByParticipant(
        participantId: String,
        startDocumentId: String?,
        limit: Int?,
    ): Flow<PaginationElement> = exchangeDataLogic.findMainExchangeDataIdsByParticipant(participantId, startDocumentId, limit)
}
