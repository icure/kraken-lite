/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.base.EntityWithSecureDelegationsLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.exceptions.CreationException
import javax.security.auth.login.LoginException

interface MessageLogic : EntityPersister<Message, String>, EntityWithSecureDelegationsLogic<Message> {
    fun findMessagesByFromAddress(
        partyId: String,
        fromAddress: String,
        paginationOffset: PaginationOffset<ComplexKey>
    ): Flow<ViewQueryResultEvent>

	fun findMessagesByToAddress(partyId: String, toAddress: String, paginationOffset: PaginationOffset<ComplexKey>, reverse: Boolean = false): Flow<ViewQueryResultEvent>

    fun findMessagesByTransportGuidReceived(
        partyId: String,
        transportGuid: String?,
        paginationOffset: PaginationOffset<ComplexKey>
    ): Flow<ViewQueryResultEvent>

    fun findMessagesByTransportGuid(
        partyId: String,
        transportGuid: String?,
        paginationOffset: PaginationOffset<List<String?>>
    ): Flow<ViewQueryResultEvent>

    fun listMessageIdsByTransportGuid(hcPartyId: String, transportGuid: String?): Flow<String>

    fun findMessagesByTransportGuidSentDate(
        partyId: String,
        transportGuid: String,
        fromDate: Long,
        toDate: Long,
        paginationOffset: PaginationOffset<ComplexKey>
    ): Flow<ViewQueryResultEvent>

    suspend fun addDelegation(message: Message, delegation: Delegation): Message?

    fun createMessages(entities: Collection<Message>): Flow<Message>

    @Throws(CreationException::class, LoginException::class)
    suspend fun createMessage(message: Message): Message?

    @Throws(LoginException::class)
    suspend fun getMessage(messageId: String): Message?

    /**
     * Returns all the [Message]s for the patients whose secret foreign keys are provided and that the HCP passed as
     * parameter can access.
     *
     * @param hcPartyId the id of the healthcare party.
     * @param secretPatientKeys the secret patient keys to consider.
     * @return a [Flow] of [Message]s matching the criterion.
     */
    fun listMessagesByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<Message>

    fun setStatus(messages: Collection<Message>, status: Int): Flow<Message>
    fun setReadStatus(messages: Collection<Message>, userId: String, status: Boolean, time: Long): Flow<Message>

    /**
     * Finds all the [Message]s related to a specific HCP in a format for pagination.
     *
     * @param hcPartyId the id of the HCP.
     * @param paginationOffset a [PaginationOffset] that marks the start of the found messages.
     * @return a [Flow] of [Message]s wrapped in [ViewQueryResultEvent]s, for pagination.
     */
    fun findForHcPartySortedByReceived(hcPartyId: String, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>

    suspend fun addDelegations(message: Message, delegations: List<Delegation>): Message?
    fun getMessageChildren(messageId: String): Flow<Message>
    fun getMessagesChildren(parentIds: List<String>): Flow<List<Message>>
    fun getMessagesByTransportGuids(hcpId: String, transportGuids: Set<String>): Flow<Message>
    fun listMessagesByInvoiceIds(ids: List<String>): Flow<Message>
    fun listMessagesByExternalRefs(hcPartyId: String, externalRefs: List<String>): Flow<Message>
    fun solveConflicts(limit: Int? = null): Flow<IdAndRev>
    fun filterMessages(
        paginationOffset: PaginationOffset<Nothing>,
        filter: FilterChain<Message>
    ): Flow<ViewQueryResultEvent>
}
