package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.InvoiceLogic
import org.taktik.icure.asyncservice.InvoiceService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.data.LabelledOccurence
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.InvoiceType
import org.taktik.icure.entities.embed.InvoicingCode
import org.taktik.icure.entities.embed.MediumType
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.pagination.PaginationElement

@Service
class InvoiceServiceImpl(
	private val invoiceLogic: InvoiceLogic
) : InvoiceService {
	override suspend fun createInvoice(invoice: Invoice): Invoice? = invoiceLogic.createInvoice(invoice)

	override suspend fun getInvoice(invoiceId: String): Invoice? = invoiceLogic.getInvoice(invoiceId)

	override fun getInvoices(ids: List<String>): Flow<Invoice> = invoiceLogic.getInvoices(ids)

	override suspend fun modifyInvoice(invoice: Invoice): Invoice? = invoiceLogic.modifyInvoice(invoice)

	override fun modifyInvoices(invoices: List<Invoice>): Flow<Invoice> = invoiceLogic.modifyEntities(invoices)

	override suspend fun addDelegation(invoiceId: String, delegation: Delegation): Invoice? = invoiceLogic.addDelegation(invoiceId, delegation)
	override fun findInvoicesByAuthor(
		hcPartyId: String,
		fromDate: Long?,
		toDate: Long?,
		paginationOffset: PaginationOffset<ComplexKey>
	): Flow<PaginationElement> = invoiceLogic.findInvoicesByAuthor(hcPartyId, fromDate, toDate, paginationOffset)
	override fun listInvoicesByHcPartyContacts(hcPartyId: String, contactIds: Set<String>): Flow<Invoice> = invoiceLogic.listInvoicesByHcPartyContacts(hcPartyId, contactIds)

	override fun listInvoicesByHcPartyAndRecipientIds(hcPartyId: String, recipientIds: Set<String?>): Flow<Invoice> = invoiceLogic.listInvoicesByHcPartyAndRecipientIds(hcPartyId, recipientIds)
	@Suppress("DEPRECATION")
	@Deprecated("This method cannot include results with secure delegations, use listInvoiceIdsByDataOwnerPatientInvoiceDate instead")
	override fun listInvoicesByHcPartyAndPatientSfks(hcPartyId: String, secretPatientKeys: Set<String>): Flow<Invoice> = invoiceLogic.listInvoicesByHcPartyAndPatientSfks(hcPartyId, secretPatientKeys)
	override fun listInvoiceIdsByDataOwnerDecisionReference(dataOwnerId: String, decisionReference: String): Flow<String> = invoiceLogic.listInvoiceIdsByDataOwnerDecisionReference(dataOwnerId, decisionReference)

	override fun listInvoiceIdsByDataOwnerPatientInvoiceDate(
		dataOwnerId: String,
		secretForeignKeys: Set<String>,
		startDate: Long?,
		endDate: Long?,
		descending: Boolean
	): Flow<String> = invoiceLogic.listInvoiceIdsByDataOwnerPatientInvoiceDate(dataOwnerId, secretForeignKeys, startDate, endDate, descending)
	override fun listInvoicesByHcPartySentMediumTypeInvoiceTypeSentDate(
		hcPartyId: String,
		sentMediumType: MediumType,
		invoiceType: InvoiceType,
		sent: Boolean,
		fromDate: Long?,
		toDate: Long?
	): Flow<Invoice> = invoiceLogic.listInvoicesByHcPartySentMediumTypeInvoiceTypeSentDate(hcPartyId, sentMediumType, invoiceType, sent, fromDate, toDate)

	override fun listInvoicesByHcPartySendingModeStatus(
		hcPartyId: String,
		sendingMode: String?,
		status: String?,
		fromDate: Long?,
		toDate: Long?
	): Flow<Invoice> = invoiceLogic.listInvoicesByHcPartySendingModeStatus(hcPartyId, sendingMode, status, fromDate, toDate)

	override fun listInvoicesByHcPartyAndGroupId(hcPartyId: String, inputGroupId: String): Flow<Invoice> = invoiceLogic.listInvoicesByHcPartyAndGroupId(hcPartyId, inputGroupId)

	override fun listInvoicesByHcPartyAndRecipientIdsUnsent(
		hcPartyId: String,
		recipientIds: Set<String?>
	): Flow<Invoice> = invoiceLogic.listInvoicesByHcPartyAndRecipientIdsUnsent(hcPartyId, recipientIds)

	override fun listInvoicesByHcPartyAndPatientSksUnsent(
		hcPartyId: String,
		secretPatientKeys: Set<String>
	): Flow<Invoice> = invoiceLogic.listInvoicesByHcPartyAndPatientSksUnsent(hcPartyId, secretPatientKeys)

	override fun listInvoicesByServiceIds(serviceIds: Set<String>): Flow<Invoice> = invoiceLogic.listInvoicesByServiceIds(serviceIds)

	override suspend fun mergeInvoices(hcPartyId: String, invoicesIds: List<String>, destination: Invoice?): Invoice? = invoiceLogic.mergeInvoices(hcPartyId, getInvoices(invoicesIds).toList(), destination)

	override suspend fun validateInvoice(
		hcPartyId: String,
		invoice: Invoice,
		refScheme: String,
		forcedValue: String?
	): Invoice? = invoiceLogic.validateInvoice(hcPartyId, invoice, refScheme, forcedValue)

	override fun appendCodes(
		hcPartyId: String,
		userId: String,
		insuranceId: String?,
		secretPatientKeys: Set<String>,
		type: InvoiceType,
		sentMediumType: MediumType,
		invoicingCodes: List<InvoicingCode>,
		invoiceId: String?,
		invoiceGraceTime: Int?
	): Flow<Invoice> = invoiceLogic.appendCodes(hcPartyId, userId, insuranceId, secretPatientKeys, type, sentMediumType, invoicingCodes, invoiceId, invoiceGraceTime)

	override suspend fun addDelegations(invoiceId: String, delegations: List<Delegation>): Invoice? = invoiceLogic.addDelegations(invoiceId, delegations)

	override fun removeCodes(
		userId: String,
		secretPatientKeys: Set<String>,
		serviceId: String,
		inputTarificationIds: List<String>
	): Flow<Invoice> = invoiceLogic.removeCodes(userId, secretPatientKeys, serviceId, inputTarificationIds)

	override fun listInvoicesHcpsByStatus(status: String, from: Long?, to: Long?, hcpIds: List<String>): Flow<Invoice> = invoiceLogic.listInvoicesHcpsByStatus(status, from, to, hcpIds)

	override fun solveConflicts(limit: Int?, ids: List<String>?) = invoiceLogic.solveConflicts(limit, ids)

	override suspend fun getTarificationsCodesOccurrences(
		hcPartyId: String,
		minOccurrences: Long
	): List<LabelledOccurence> = invoiceLogic.getTarificationsCodesOccurrences(hcPartyId, minOccurrences)

	override fun listInvoicesIdsByTarificationsByCode(
		hcPartyId: String,
		codeCode: String,
		startValueDate: Long,
		endValueDate: Long
	): Flow<String> = invoiceLogic.listInvoicesIdsByTarificationsByCode(hcPartyId, codeCode, startValueDate, endValueDate)

	override fun filter(filter: FilterChain<Invoice>): Flow<Invoice> = invoiceLogic.filter(filter)

	override fun getInvoicesForUsersAndInsuranceIds(userIds: List<String>?): Flow<Invoice> = invoiceLogic.getInvoicesForUsersAndInsuranceIds(userIds)

	override fun getUnsentInvoicesForUsersAndInsuranceIds(userIds: List<String>?): Flow<Invoice> = invoiceLogic.getUnsentInvoicesForUsersAndInsuranceIds(userIds)

	override fun createInvoices(invoices: Collection<Invoice>): Flow<Invoice> = invoiceLogic.createEntities(invoices)
	override fun matchInvoicesBy(filter: AbstractFilter<Invoice>): Flow<String> = invoiceLogic.matchEntitiesBy(filter)
	override fun deleteInvoices(ids: List<IdAndRev>): Flow<DocIdentifier> = invoiceLogic.deleteEntities(ids)
	override suspend fun deleteInvoice(id: String, rev: String?): DocIdentifier = invoiceLogic.deleteEntity(id, rev)
	override suspend fun purgeInvoice(id: String, rev: String): DocIdentifier = invoiceLogic.purgeEntity(id, rev)
	override suspend fun undeleteInvoice(id: String, rev: String): Invoice = invoiceLogic.undeleteEntity(id, rev)
	override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<Invoice>> = invoiceLogic.bulkShareOrUpdateMetadata(requests)
}
