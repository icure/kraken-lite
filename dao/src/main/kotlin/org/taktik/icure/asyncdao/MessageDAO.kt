/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Message

interface MessageDAO : GenericDAO<Message> {
	fun listMessagesByFromAddressAndActor(datastoreInformation: IDatastoreInformation, partyId: String, fromAddress: String, actorKeys: List<String>?): Flow<Message>
	fun listMessagesByToAddressAndActor(datastoreInformation: IDatastoreInformation, partyId: String, toAddress: String, actorKeys: List<String>?): Flow<Message>
	fun listMessagesByTransportGuidAndActor(datastoreInformation: IDatastoreInformation, partyId: String, transportGuid: String, actorKeys: List<String>?): Flow<Message>
	fun listMessagesByFromAddress(datastoreInformation: IDatastoreInformation, partyId: String, fromAddress: String, paginationOffset: PaginationOffset<List<*>>, reverse: Boolean = false): Flow<ViewQueryResultEvent>

	fun findMessagesByToAddress(datastoreInformation: IDatastoreInformation, partyId: String, toAddress: String, paginationOffset: PaginationOffset<List<*>>, reverse: Boolean = false): Flow<ViewQueryResultEvent>
	fun findMessagesByHcPartySortedByReceived(
		datastoreInformation: IDatastoreInformation,
		partyId: String,
		paginationOffset: PaginationOffset<ComplexKey>
	): Flow<ViewQueryResultEvent>
	fun findMessagesByTransportGuid(
		datastoreInformation: IDatastoreInformation,
		partyId: String,
		transportGuid: String?,
		paginationOffset: PaginationOffset<List<*>>
	): Flow<ViewQueryResultEvent>
	fun listMessageIdsByTransportGuid(datastoreInformation: IDatastoreInformation, hcPartyId: String, transportGuid: String?): Flow<String>
	fun findMessagesByTransportGuidReceived(
		datastoreInformation: IDatastoreInformation,
		partyId: String,
		transportGuid: String?,
		paginationOffset: PaginationOffset<List<*>>
	): Flow<ViewQueryResultEvent>

	fun findMessagesByTransportGuidAndSentDate(datastoreInformation: IDatastoreInformation, partyId: String, transportGuid: String, fromDate: Long, toDate: Long, paginationOffset: PaginationOffset<List<*>>): Flow<ViewQueryResultEvent>
	fun listMessagesByHcPartyAndPatient(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, secretPatientKeys: List<String>): Flow<Message>
	fun getChildren(datastoreInformation: IDatastoreInformation, messageId: String): Flow<Message>
	fun listMessagesByInvoiceIds(datastoreInformation: IDatastoreInformation, invoiceIds: Set<String>): Flow<Message>
	fun getMessagesByTransportGuids(datastoreInformation: IDatastoreInformation, hcPartyId: String, transportGuids: Collection<String>): Flow<Message>
	fun getMessagesByExternalRefs(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, externalRefs: Set<String>): Flow<Message>
	fun listConflicts(datastoreInformation: IDatastoreInformation): Flow<Message>
	fun getMessagesChildren(datastoreInformation: IDatastoreInformation, parentIds: List<String>): Flow<List<Message>>
	fun findMessagesByIds(datastoreInformation: IDatastoreInformation, messageIds: Collection<String>): Flow<ViewQueryResultEvent>
}
