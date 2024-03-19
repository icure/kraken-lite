package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.MessageLogic
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.asyncservice.MessageService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.pagination.PaginationElement

@Service
class MessageServiceImpl(
    private val messageLogic: MessageLogic,
    private val sessionInformationProvider: SessionInformationProvider
) : MessageService {
    override fun findMessagesByFromAddress(
        hcPartyId: String,
        fromAddress: String,
        paginationOffset: PaginationOffset<ComplexKey>
    ): Flow<PaginationElement> = messageLogic.findMessagesByFromAddress(hcPartyId, fromAddress, paginationOffset)

    override fun findMessagesByToAddress(
        hcPartyId: String,
        toAddress: String,
        paginationOffset: PaginationOffset<ComplexKey>,
        reverse: Boolean?
    ): Flow<PaginationElement> = messageLogic.findMessagesByToAddress(hcPartyId, toAddress, paginationOffset, reverse ?: false)

    override fun findMessagesByTransportGuidReceived(
        hcPartyId: String,
        transportGuid: String?,
        paginationOffset: PaginationOffset<ComplexKey>
    ): Flow<PaginationElement> = messageLogic.findMessagesByTransportGuidReceived(hcPartyId, transportGuid, paginationOffset)

    override fun findMessagesByTransportGuid(
        hcPartyId: String,
        transportGuid: String?,
        paginationOffset: PaginationOffset<ComplexKey>
    ): Flow<PaginationElement> = messageLogic.findMessagesByTransportGuid(hcPartyId, transportGuid, paginationOffset)
    override fun findMessagesByTransportGuidSentDate(
        hcPartyId: String,
        transportGuid: String,
        fromDate: Long,
        toDate: Long,
        paginationOffset: PaginationOffset<ComplexKey>
    ): Flow<PaginationElement> = messageLogic.findMessagesByTransportGuidSentDate(hcPartyId, transportGuid, fromDate, toDate, paginationOffset)

    override suspend fun addDelegation(messageId: String, delegation: Delegation): Message? = getMessage(messageId)?.let {
        messageLogic.addDelegation(it, delegation)
    }

    override suspend fun createMessage(message: Message): Message? = messageLogic.createMessage(message)

    override fun createMessages(entities: Collection<Message>): Flow<Message> = messageLogic.createMessages(entities)

    override suspend fun getMessage(messageId: String): Message? = messageLogic.getMessage(messageId)

    override suspend fun modifyMessage(message: Message): Message? = messageLogic.modifyEntities(flowOf(message)).singleOrNull()

    override fun listMessagesByCurrentHCPartySecretPatientKeys(secretPatientKeys: List<String>): Flow<Message> = flow {
        emitAll(
            messageLogic.listMessagesByHCPartySecretPatientKeys(sessionInformationProvider.getCurrentHealthcarePartyId(), secretPatientKeys)
        )
    }
    override fun listMessagesByCurrentHCPartySecretPatientKey(
        secretPatientKey: String,
        paginationOffset: PaginationOffset<ComplexKey>
    ): Flow<PaginationElement> = flow {
        emitAll(
            messageLogic.listMessagesByHcPartySecretPatientKey(sessionInformationProvider.getCurrentHealthcarePartyId(), secretPatientKey, paginationOffset)
        )
    }

    override fun setStatus(messageIds: List<String>, status: Int): Flow<Message> = flow {
        emitAll(
            messageLogic.getEntities(messageIds).let {
                messageLogic.setStatus(it.toList(), status)
            }
        )
    }

    override fun setReadStatus(messageIds: List<String>, userId: String, status: Boolean, time: Long): Flow<Message> = flow {
        emitAll(
            messageLogic.getEntities(messageIds).let {
                messageLogic.setReadStatus(it.toList(), userId, status, time)
            }
        )
    }

    override fun findForCurrentHcPartySortedByReceived(paginationOffset: PaginationOffset<ComplexKey>): Flow<PaginationElement> = flow {
        emitAll(
            messageLogic.findForHcPartySortedByReceived(sessionInformationProvider.getCurrentHealthcarePartyId(), paginationOffset)
        )
    }

    override suspend fun addDelegations(messageId: String, delegations: List<Delegation>): Message? = getMessage(messageId)?.let {
        messageLogic.addDelegations(it, delegations)
    }

    override fun getMessageChildren(messageId: String): Flow<Message> = messageLogic.getMessageChildren(messageId)

    override fun getMessagesChildren(parentIds: List<String>): Flow<List<Message>> = messageLogic.getMessagesChildren(parentIds)

    override fun getMessagesByTransportGuids(hcpId: String, transportGuids: Set<String>): Flow<Message> = messageLogic.getMessagesByTransportGuids(hcpId, transportGuids)

    override fun listMessagesByInvoiceIds(ids: List<String>): Flow<Message> = messageLogic.listMessagesByInvoiceIds(ids)

    override fun listMessagesByExternalRefs(hcPartyId: String, externalRefs: List<String>): Flow<Message> = messageLogic.listMessagesByExternalRefs(hcPartyId, externalRefs)

    override fun solveConflicts(limit: Int?): Flow<IdAndRev> = messageLogic.solveConflicts(limit)

    override fun filterMessages(
        paginationOffset: PaginationOffset<Nothing>,
        filter: FilterChain<Message>
    ): Flow<ViewQueryResultEvent> = messageLogic.filterMessages(paginationOffset, filter)

    override fun deleteMessages(identifiers: Collection<String>): Flow<DocIdentifier> = messageLogic.deleteEntities(identifiers)

    override suspend fun deleteMessage(id: String): DocIdentifier = messageLogic.deleteEntities(flowOf(id)).single()

    override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<Message>> = messageLogic.bulkShareOrUpdateMetadata(requests)
}