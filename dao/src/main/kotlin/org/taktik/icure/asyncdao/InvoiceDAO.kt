/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowNoDoc
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.embed.InvoiceType
import org.taktik.icure.entities.embed.MediumType

interface InvoiceDAO : GenericDAO<Invoice> {
	fun findInvoicesByHcParty(datastoreInformation: IDatastoreInformation, hcParty: String, fromDate: Long?, toDate: Long?, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>

	fun listInvoicesByHcPartyAndContacts(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, contactId: Set<String>): Flow<Invoice>

	fun listInvoicesByHcPartyAndReferences(datastoreInformation: IDatastoreInformation, hcParty: String, invoiceReferences: Set<String>?): Flow<Invoice>

	fun listInvoicesByHcPartyAndReferences(datastoreInformation: IDatastoreInformation, hcParty: String, from: String?, to: String?, descending: Boolean, limit: Int): Flow<Invoice>

	fun listInvoicesByHcPartyAndGroupId(
		datastoreInformation: IDatastoreInformation,
		hcParty: String,
		inputGroupId: String
	): Flow<Invoice>

	fun listInvoicesByHcPartyAndRecipientIds(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, recipientIds: Set<String?>): Flow<Invoice>

	fun listInvoicesByHcPartyAndPatientFk(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, secretPatientKeys: Set<String>): Flow<Invoice>

	fun listInvoicesByHcPartyAndRecipientIdsUnsent(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, recipientIds: Set<String?>): Flow<Invoice>

	fun listInvoicesByHcPartyAndPatientFkUnsent(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, secretPatientKeys: Set<String>): Flow<Invoice>

	fun listInvoicesByHcPartySentMediumTypeInvoiceTypeSentDate(datastoreInformation: IDatastoreInformation, hcParty: String, sentMediumType: MediumType, invoiceType: InvoiceType, sent: Boolean, fromDate: Long?, toDate: Long?): Flow<Invoice>

	fun listInvoicesByHcPartySendingModeStatus(datastoreInformation: IDatastoreInformation, hcParty: String, sendingMode: String?, status: String?, fromDate: Long?, toDate: Long?): Flow<Invoice>

	fun listInvoicesByServiceIds(datastoreInformation: IDatastoreInformation, serviceIds: Set<String>): Flow<Invoice>

	fun listInvoicesHcpsByStatus(datastoreInformation: IDatastoreInformation, status: String, from: Long?, to: Long?, hcpIds: List<String>): Flow<Invoice>

	fun listConflicts(datastoreInformation: IDatastoreInformation): Flow<Invoice>

	fun listInvoiceIdsByTarificationsAndCode(datastoreInformation: IDatastoreInformation, hcPartyId: String, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>

	fun listInvoiceIdsByTarificationsByCode(datastoreInformation: IDatastoreInformation, hcPartyId: String, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>
	fun listTarificationsFrequencies(datastoreInformation: IDatastoreInformation, hcPartyId: String): Flow<ViewRowNoDoc<ComplexKey, Long>>
}
