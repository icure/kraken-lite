/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.asyncservice

import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.AccessDeniedException
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asyncservice.base.EntityWithSecureDelegationsService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.exceptions.CreationException
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.exceptions.NotFoundRequestException
import javax.security.auth.login.LoginException

interface MessageService : EntityWithSecureDelegationsService<Message> {
    fun findMessagesByFromAddress(
        hcPartyId: String,
        fromAddress: String,
        paginationOffset: PaginationOffset<List<*>>
    ): Flow<ViewQueryResultEvent>

    fun findMessagesByToAddress(
        hcPartyId: String,
        toAddress: String,
        paginationOffset: PaginationOffset<List<*>>,
        reverse: Boolean?
    ): Flow<ViewQueryResultEvent>

    fun findMessagesByTransportGuidReceived(
        hcPartyId: String,
        transportGuid: String?,
        paginationOffset: PaginationOffset<List<*>>
    ): Flow<ViewQueryResultEvent>

    fun findMessagesByTransportGuid(
        hcPartyId: String,
        transportGuid: String?,
        paginationOffset: PaginationOffset<List<*>>
    ): Flow<ViewQueryResultEvent>

    fun findMessagesByTransportGuidSentDate(
        hcPartyId: String,
        transportGuid: String,
        fromDate: Long,
        toDate: Long,
        paginationOffset: PaginationOffset<List<*>>
    ): Flow<ViewQueryResultEvent>

    suspend fun addDelegation(messageId: String, delegation: Delegation): Message?

    @Throws(CreationException::class, LoginException::class)
    suspend fun createMessage(message: Message): Message?

    fun createMessages(entities: Collection<Message>): Flow<Message>

    @Throws(LoginException::class)
    suspend fun getMessage(messageId: String): Message?

    @Throws(MissingRequirementsException::class)
    suspend fun modifyMessage(message: Message): Message?

    /**
     * Returns all the [Message]s that the current healthcare party can access, given the secret patient keys of the
     * patients related to the messages to retrieve.
     *
     * @param secretPatientKeys the secret patient keys.
     * @return a [Flow] of [Message]s matching the criterion.
     */
    fun listMessagesByCurrentHCPartySecretPatientKeys(secretPatientKeys: List<String>): Flow<Message>

    fun setStatus(messageIds: List<String>, status: Int): Flow<Message>
    fun setReadStatus(messageIds: List<String>, userId: String, status: Boolean, time: Long): Flow<Message>

    fun findForCurrentHcPartySortedByReceived(paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>

    suspend fun addDelegations(messageId: String, delegations: List<Delegation>): Message?
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

    /**
     * Deletes [Message]s in batch.
     * If the user does not meet the precondition to delete [Message]s, an error will be thrown.
     * If the current user does not have the permission to delete one or more elements in
     * the batch, then those elements will not be deleted and no error will be thrown.
     *
     * @param identifiers a [Collection] containing the ids of the [Message]s to delete.
     * @return a [Flow] containing the [DocIdentifier]s of the [Message]s that were successfully deleted.
     */
    fun deleteMessages(identifiers: Collection<String>): Flow<DocIdentifier>

    /**
     * Deletes a [Message].
     *
     * @param id the id of the [Message] to delete.
     * @return a [DocIdentifier] related to the [Message] if the operation completes successfully.
     * @throws [AccessDeniedException] if the current user does not have the permission to delete the [Message].
     * @throws [NotFoundRequestException] if an [Message] with the specified [id] does not exist.
     */
    suspend fun deleteMessage(id: String): DocIdentifier
}
