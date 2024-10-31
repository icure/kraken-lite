package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asyncservice.ContactService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.data.LabelledOccurence
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.pagination.PaginationElement

@org.springframework.stereotype.Service
class ContactServiceImpl(
    private val contactLogic: ContactLogic
) : ContactService {
    override suspend fun getContact(id: String): Contact? = contactLogic.getContact(id)

    override fun getContacts(selectedIds: Collection<String>): Flow<Contact> = contactLogic.getContacts(selectedIds)

    override fun findContactsByIds(selectedIds: Collection<String>): Flow<ViewQueryResultEvent> = contactLogic.findContactsByIds(selectedIds)

    @Suppress("DEPRECATION")
    @Deprecated("This method cannot include results with secure delegations, use listContactIdsByDataOwnerPatientOpeningDate instead")
    override fun listContactsByHCPartyAndPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<Contact> = contactLogic.listContactsByHCPartyAndPatient(hcPartyId, secretPatientKeys)

    override fun listContactIdsByDataOwnerPatientOpeningDate(
        dataOwnerId: String,
        secretForeignKeys: Set<String>,
        startDate: Long?,
        endDate: Long?,
        descending: Boolean
    ): Flow<String> = contactLogic.listContactIdsByDataOwnerPatientOpeningDate(dataOwnerId, secretForeignKeys, startDate, endDate, descending)

    override suspend fun addDelegation(contactId: String, delegation: Delegation): Contact? = contactLogic.addDelegation(contactId, delegation)

    override suspend fun createContact(contact: Contact): Contact? = contactLogic.createContact(contact)
    override fun deleteContacts(ids: List<IdAndRev>): Flow<DocIdentifier> = contactLogic.deleteEntities(ids)
    override suspend fun deleteContact(id: String, rev: String?): DocIdentifier = contactLogic.deleteEntity(id, rev)
    override suspend fun purgeContact(id: String, rev: String): DocIdentifier = contactLogic.purgeEntity(id, rev)
    override suspend fun undeleteContact(id: String, rev: String): Contact = contactLogic.undeleteEntity(id, rev)

    override suspend fun modifyContact(contact: Contact): Contact? = contactLogic.modifyEntities(setOf(contact)).single()

    override suspend fun getService(serviceId: String): Service? = contactLogic.getServices(listOf(serviceId)).singleOrNull()

    override fun getServices(selectedServiceIds: Collection<String>): Flow<Service> = contactLogic.getServices(selectedServiceIds)

    override fun getServicesLinkedTo(ids: List<String>, linkType: String?): Flow<Service> = contactLogic.getServicesLinkedTo(ids, linkType)

    override fun listServicesByAssociationId(associationId: String): Flow<Service> = contactLogic.listServicesByAssociationId(associationId)

    override fun listContactsByHcPartyAndFormId(hcPartyId: String, formId: String): Flow<Contact> = contactLogic.listContactsByHcPartyAndFormId(hcPartyId, formId)

    override fun listContactsByHcPartyServiceId(hcPartyId: String, formId: String): Flow<Contact> = contactLogic.listContactsByHcPartyServiceId(hcPartyId, formId)

    override fun listContactsByExternalId(externalId: String): Flow<Contact> = contactLogic.listContactsByExternalId(externalId)

    override fun listServicesByHcPartyAndHealthElementIds(
        hcPartyId: String,
        healthElementIds: List<String>
    ): Flow<Service> = contactLogic.listServicesByHcPartyAndHealthElementIds(hcPartyId, healthElementIds)

    override suspend fun getServiceCodesOccurences(
        hcPartyId: String,
        codeType: String,
        minOccurences: Long
    ): List<LabelledOccurence> = contactLogic.getServiceCodesOccurences(hcPartyId, codeType, minOccurences)

    override fun listContactsByHcPartyAndFormIds(hcPartyId: String, ids: List<String>): Flow<Contact> = contactLogic.listContactsByHcPartyAndFormIds(hcPartyId, ids)

    override fun filterContacts(
        paginationOffset: PaginationOffset<Nothing>,
        filter: FilterChain<Contact>
    ): Flow<ViewQueryResultEvent> = contactLogic.filterContacts(paginationOffset, filter)

    override fun filterServices(
        paginationOffset: PaginationOffset<Nothing>,
        filter: FilterChain<Service>
    ): Flow<Service> = contactLogic.filterServices(paginationOffset, filter)

    override fun solveConflicts(limit: Int?, ids: List<String>?) = contactLogic.solveConflicts(limit, ids)

    override fun listContactsByOpeningDate(
        hcPartyId: String,
        startOpeningDate: Long,
        endOpeningDate: Long,
        offset: PaginationOffset<ComplexKey>
    ): Flow<PaginationElement> = contactLogic.listContactsByOpeningDate(hcPartyId, startOpeningDate, endOpeningDate, offset)

    override suspend fun addDelegations(contactId: String, delegations: List<Delegation>): Contact? = contactLogic.addDelegations(contactId, delegations)

    override fun createContacts(contacts: Collection<Contact>): Flow<Contact> = contactLogic.createContacts(contacts.asFlow())

    override fun createContacts(contacts: Flow<Contact>): Flow<Contact> = contactLogic.createContacts(contacts)

    override fun modifyContacts(contacts: Collection<Contact>): Flow<Contact> = contactLogic.modifyEntities(contacts)
    override fun matchContactsBy(filter: AbstractFilter<Contact>): Flow<String> = contactLogic.matchEntitiesBy(filter)

    override fun matchServicesBy(filter: AbstractFilter<Service>): Flow<String> = contactLogic.matchEntitiesBy(filter)

    override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<Contact>> = contactLogic.bulkShareOrUpdateMetadata(requests)
}
