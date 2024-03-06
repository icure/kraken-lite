package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asyncservice.PatientService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.Sorting
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.pagination.PaginationElement
import java.time.Instant

@Service
class PatientServiceImpl(
    private val patientLogic: PatientLogic
) : PatientService {
    override suspend fun countByHcParty(healthcarePartyId: String): Int = patientLogic.countByHcParty(healthcarePartyId)

    override fun listByHcPartyIdsOnly(healthcarePartyId: String): Flow<String> = patientLogic.listByHcPartyIdsOnly(healthcarePartyId)

    override fun listByHcPartyAndSsinIdsOnly(ssin: String, healthcarePartyId: String): Flow<String> = patientLogic.listByHcPartyAndSsinIdsOnly(ssin, healthcarePartyId)

    override fun listByHcPartyAndSsinsIdsOnly(ssins: Collection<String>, healthcarePartyId: String): Flow<String> = patientLogic.listByHcPartyAndSsinsIdsOnly(ssins, healthcarePartyId)

    override fun listByHcPartyDateOfBirthIdsOnly(date: Int, healthcarePartyId: String): Flow<String> = patientLogic.listByHcPartyDateOfBirthIdsOnly(date, healthcarePartyId)

    override fun listByHcPartyGenderEducationProfessionIdsOnly(
        healthcarePartyId: String,
        gender: Gender?,
        education: String?,
        profession: String?
    ): Flow<String> = patientLogic.listByHcPartyGenderEducationProfessionIdsOnly(healthcarePartyId, gender, education, profession)

    override fun listByHcPartyDateOfBirthIdsOnly(
        startDate: Int?,
        endDate: Int?,
        healthcarePartyId: String
    ): Flow<String> = patientLogic.listByHcPartyDateOfBirthIdsOnly(startDate, endDate, healthcarePartyId)

    override fun listByHcPartyNameContainsFuzzyIdsOnly(searchString: String?, healthcarePartyId: String): Flow<String> = patientLogic.listByHcPartyNameContainsFuzzyIdsOnly(searchString, healthcarePartyId)

    override fun listByHcPartyName(searchString: String?, healthcarePartyId: String): Flow<String> = patientLogic.listByHcPartyName(searchString, healthcarePartyId)

    override fun listByHcPartyAndExternalIdsOnly(externalId: String?, healthcarePartyId: String): Flow<String> = patientLogic.listByHcPartyAndExternalIdsOnly(externalId, healthcarePartyId)

    override fun listPatientIdsByHcPartyAndTelecomOnly(searchString: String?, healthcarePartyId: String): Flow<String> = patientLogic.listPatientIdsByHcPartyAndTelecomOnly(searchString, healthcarePartyId)

    override fun listPatientIdsByHcPartyAndAddressOnly(searchString: String?, healthcarePartyId: String): Flow<String> = patientLogic.listPatientIdsByHcPartyAndAddressOnly(searchString, healthcarePartyId)

    override fun listByHcPartyAndActiveIdsOnly(active: Boolean, healthcarePartyId: String): Flow<String> = patientLogic.listByHcPartyAndActiveIdsOnly(active, healthcarePartyId)

    override fun listOfMergesAfter(date: Long?): Flow<Patient> = patientLogic.listOfMergesAfter(date)
    override fun findByHcPartyIdsOnly(
        healthcarePartyId: String,
        offset: PaginationOffset<ComplexKey>
    ): Flow<PaginationElement> = patientLogic.findByHcPartyIdsOnly(healthcarePartyId, offset)
    override fun findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(
        healthcarePartyId: String,
        offset: PaginationOffset<ComplexKey>,
        searchString: String?,
        sorting: Sorting<PatientLogic.Companion.PatientSearchField>
    ): Flow<PaginationElement> = patientLogic.findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(healthcarePartyId, offset, searchString, sorting)

    override fun listPatients(
        paginationOffset: PaginationOffset<*>,
        filterChain: FilterChain<Patient>,
        sort: String?,
        desc: Boolean?
    ): Flow<ViewQueryResultEvent> = patientLogic.listPatients(paginationOffset, filterChain, sort, desc)

    override fun findByHcPartyNameContainsFuzzy(
        searchString: String?,
        healthcarePartyId: String,
        offset: PaginationOffset<ComplexKey>,
        descending: Boolean
    ): Flow<ViewQueryResultEvent> = patientLogic.findByHcPartyNameContainsFuzzy(searchString, healthcarePartyId, offset, descending)

    override fun findOfHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(
        healthcarePartyId: String,
        offset: PaginationOffset<ComplexKey>,
        searchString: String?,
        sorting: Sorting<PatientLogic.Companion.PatientSearchField>
    ): Flow<PaginationElement> = patientLogic.findOfHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(healthcarePartyId, offset, searchString, sorting)

    override fun findByHcPartyAndSsin(
        ssin: String?,
        healthcarePartyId: String,
        paginationOffset: PaginationOffset<List<String>>
    ): Flow<ViewQueryResultEvent> = patientLogic.findByHcPartyAndSsin(ssin, healthcarePartyId, paginationOffset)

    override fun findByHcPartyDateOfBirth(
        date: Int?,
        healthcarePartyId: String,
        paginationOffset: PaginationOffset<List<String>>
    ): Flow<ViewQueryResultEvent> = patientLogic.findByHcPartyDateOfBirth(date, healthcarePartyId, paginationOffset)

    override fun findByHcPartyModificationDate(
        start: Long?,
        end: Long?,
        healthcarePartyId: String,
        descending: Boolean,
        paginationOffset: PaginationOffset<List<String>>
    ): Flow<ViewQueryResultEvent> = patientLogic.findByHcPartyModificationDate(start, end, healthcarePartyId, descending, paginationOffset)

    override suspend fun findByUserId(id: String): Patient? = patientLogic.findByUserId(id)

    override suspend fun getPatient(patientId: String): Patient? = patientLogic.getPatient(patientId)

    override fun findByHcPartyAndIdentifier(healthcarePartyId: String, system: String, id: String): Flow<Patient> = patientLogic.findByHcPartyAndIdentifier(healthcarePartyId, system, id)

    override fun getPatients(patientIds: List<String>): Flow<Patient> = patientLogic.getPatients(patientIds)

    override suspend fun addDelegation(patientId: String, delegation: Delegation): Patient? = patientLogic.addDelegation(patientId, delegation)

    override suspend fun addDelegations(patientId: String, delegations: Collection<Delegation>): Patient? = patientLogic.addDelegations(patientId, delegations)

    override suspend fun createPatient(patient: Patient): Patient? = patientLogic.createPatient(patient)

    override fun createPatients(patients: List<Patient>): Flow<Patient> = patientLogic.createPatients(patients)

    override suspend fun modifyPatient(patient: Patient): Patient? = patientLogic.modifyPatient(patient)

    override fun modifyPatients(patients: List<Patient>): Flow<Patient> = patientLogic.modifyPatients(patients)

    override suspend fun modifyPatientReferral(
        patient: Patient,
        referralId: String?,
        start: Instant?,
        end: Instant?
    ): Patient? = patientLogic.modifyPatientReferral(patient, referralId, start, end)

    override suspend fun getByExternalId(externalId: String): Patient? = patientLogic.getByExternalId(externalId)

    override fun solveConflicts(limit: Int?): Flow<IdAndRev> = patientLogic.solveConflicts(limit)

    @Suppress("DEPRECATION")
    @Deprecated("A DataOwner may now have multiple AES Keys. Use getAesExchangeKeysForDelegate instead")
    override suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String> = patientLogic.getHcPartyKeysForDelegate(healthcarePartyId)

    override suspend fun getAesExchangeKeysForDelegate(healthcarePartyId: String): Map<String, Map<String, Map<String, String>>> = patientLogic.getAesExchangeKeysForDelegate(healthcarePartyId)
    override fun listOfPatientsModifiedAfter(
        date: Long,
        paginationOffset: PaginationOffset<Long>
    ): Flow<PaginationElement> = patientLogic.listOfPatientsModifiedAfter(date, paginationOffset)

    override fun getDuplicatePatientsByName(
        healthcarePartyId: String,
        paginationOffset: PaginationOffset<ComplexKey>
    ): Flow<PaginationElement> = patientLogic.getDuplicatePatientsByName(healthcarePartyId, paginationOffset)

    override fun getDuplicatePatientsBySsin(
        healthcarePartyId: String,
        paginationOffset: PaginationOffset<ComplexKey>
    ): Flow<PaginationElement> = patientLogic.getDuplicatePatientsBySsin(healthcarePartyId, paginationOffset)

    override fun fuzzySearchPatients(
        firstName: String?,
        lastName: String?,
        dateOfBirth: Int?,
        healthcarePartyId: String?
    ): Flow<Patient> = patientLogic.fuzzySearchPatients(firstName, lastName, dateOfBirth, healthcarePartyId)

    override fun deletePatients(ids: Set<String>): Flow<DocIdentifier> = patientLogic.deletePatients(ids)

    override suspend fun deletePatient(id: String): DocIdentifier = patientLogic.deletePatients(setOf(id)).single()

    override fun findDeletedPatientsByDeleteDate(
        start: Long,
        end: Long?,
        descending: Boolean,
        paginationOffset: PaginationOffset<Long>
    ): Flow<PaginationElement> = patientLogic.findDeletedPatientsByDeleteDate(start, end, descending, paginationOffset)

    override fun listDeletedPatientsByNames(firstName: String?, lastName: String?): Flow<Patient> = patientLogic.listDeletedPatientsByNames(firstName, lastName)

    override fun undeletePatients(ids: Set<String>): Flow<DocIdentifier> = patientLogic.undeletePatients(ids)

    override fun listPatientIdsByHcpartyAndIdentifiers(
        healthcarePartyId: String,
        identifiers: List<Identifier>
    ): Flow<String> = patientLogic.listPatientIdsByHcpartyAndIdentifiers(healthcarePartyId, identifiers)

    override fun getEntityIds(): Flow<String> = patientLogic.getEntityIds()

    override suspend fun mergePatients(fromId: String, expectedFromRev: String, updatedInto: Patient): Patient = patientLogic.mergePatients(fromId, expectedFromRev, updatedInto)

    override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<Patient>> = patientLogic.bulkShareOrUpdateMetadata(requests)
}
