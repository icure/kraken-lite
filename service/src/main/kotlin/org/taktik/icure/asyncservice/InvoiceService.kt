/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncservice

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncservice.base.EntityWithSecureDelegationsService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.InvoiceType
import org.taktik.icure.entities.embed.InvoicingCode
import org.taktik.icure.entities.embed.MediumType
import org.taktik.couchdb.entity.IdAndRev

interface InvoiceService : EntityWithSecureDelegationsService<Invoice> {
	suspend fun createInvoice(invoice: Invoice): Invoice?
	suspend fun deleteInvoice(invoiceId: String): DocIdentifier?

	suspend fun getInvoice(invoiceId: String): Invoice?
	fun getInvoices(ids: List<String>): Flow<Invoice>

	suspend fun modifyInvoice(invoice: Invoice): Invoice?
	fun modifyInvoices(invoices: List<Invoice>): Flow<Invoice>

	suspend fun addDelegation(invoiceId: String, delegation: Delegation): Invoice?
	fun findInvoicesByAuthor(hcPartyId: String, fromDate: Long?, toDate: Long?, paginationOffset: PaginationOffset<List<*>>): Flow<ViewQueryResultEvent>

	fun listInvoicesByHcPartyContacts(hcPartyId: String, contactIds: Set<String>): Flow<Invoice>
	fun listInvoicesByHcPartyAndRecipientIds(hcPartyId: String, recipientIds: Set<String?>): Flow<Invoice>
	fun listInvoicesByHcPartyAndPatientSks(hcPartyId: String, secretPatientKeys: Set<String>): Flow<Invoice>
	fun listInvoicesByHcPartySentMediumTypeInvoiceTypeSentDate(hcPartyId: String, sentMediumType: MediumType, invoiceType: InvoiceType, sent: Boolean, fromDate: Long?, toDate: Long?): Flow<Invoice>
	fun listInvoicesByHcPartySendingModeStatus(hcPartyId: String, sendingMode: String?, status: String?, fromDate: Long?, toDate: Long?): Flow<Invoice>
	fun listInvoicesByHcPartyAndGroupId(hcPartyId: String, inputGroupId: String): Flow<Invoice>
	fun listInvoicesByHcPartyAndRecipientIdsUnsent(hcPartyId: String, recipientIds: Set<String?>): Flow<Invoice>
	fun listInvoicesByHcPartyAndPatientSksUnsent(hcPartyId: String, secretPatientKeys: Set<String>): Flow<Invoice>
	fun listInvoicesByServiceIds(serviceIds: Set<String>): Flow<Invoice>
	suspend fun mergeInvoices(hcPartyId: String, invoicesIds: List<String>, destination: Invoice?): Invoice?
	suspend fun validateInvoice(hcPartyId: String, invoice: Invoice, refScheme: String, forcedValue: String?): Invoice?
	fun appendCodes(hcPartyId: String, userId: String, insuranceId: String?, secretPatientKeys: Set<String>, type: InvoiceType, sentMediumType: MediumType, invoicingCodes: List<InvoicingCode>, invoiceId: String?, invoiceGraceTime: Int?): Flow<Invoice>

	suspend fun addDelegations(invoiceId: String, delegations: List<Delegation>): Invoice?
	fun removeCodes(userId: String, secretPatientKeys: Set<String>, serviceId: String, inputTarificationIds: List<String>): Flow<Invoice>
	fun listInvoicesHcpsByStatus(status: String, from: Long?, to: Long?, hcpIds: List<String>): Flow<Invoice>
	fun solveConflicts(limit: Int? = null): Flow<IdAndRev>

	suspend fun getTarificationsCodesOccurrences(hcPartyId: String, minOccurrences: Long): List<org.taktik.icure.entities.data.LabelledOccurence>
	fun listInvoicesIdsByTarificationsByCode(hcPartyId: String, codeCode: String, startValueDate: Long, endValueDate: Long): Flow<String>
	fun listInvoiceIdsByTarificationsByCode(hcPartyId: String, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>

	fun filter(filter: FilterChain<Invoice>): Flow<Invoice>

	/**
	 * Returns a flow of all the [Invoice]s for the healthcare parties which user ids are passed as parameter for all
	 * the insurances id available. The elements of the flow are ordered by sentDate.
	 * If no userId is passed, all the users of the group are used instead.
	 * This method will fail if at least one of the users is not a HCP.
	 * Only users with the [Permissions.InvoiceManagement.Maintenance.CanGetInvoicesForUsersAndInsurances] can access
	 * this method.
	 * @param userIds a [List] of user ids to search the [Invoice]s. If null, all the users in the system will be used.
	 * @return a [Flow] of all the [Invoice]s, ordered by sent date.
	 */
	fun getInvoicesForUsersAndInsuranceIds(userIds: List<String>?): Flow<Invoice>

	/**
	 * Returns a flow of all the unsent [Invoice]s for the healthcare parties which user ids are passed as parameter for
	 * all the insurances id available. The elements of the flow are ordered by invoiceDate.
	 * If no userId is passed, all the users of the group are used instead.
	 * This method will fail if at least one of the users is not a HCP.
	 * Only users with the [Permissions.InvoiceManagement.Maintenance.CanGetInvoicesForUsersAndInsurances] can access
	 * this method.
	 * @param userIds a [List] of user ids to search the [Invoice]s. If null, all the users in the system will be used.
	 * @return a [Flow] of all the [Invoice]s, ordered by invoiceDate.
	 */
	fun getUnsentInvoicesForUsersAndInsuranceIds(userIds: List<String>?): Flow<Invoice>

	/**
	 * Creates a batch of [Invoice]s. It will fail if the current user does not have the permission to create invoices.
	 * @param invoices a [Collection] of [Invoice]s to create.
	 * @return a [Flow] containing the successfully created [Invoice]s.
	 */
	fun createInvoices(invoices: Collection<Invoice>): Flow<Invoice>
}
