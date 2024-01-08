/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.Identifier

interface PatientDAO : GenericDAO<Patient> {

	fun listPatientIdsByHcPartyAndName(datastoreInformation: IDatastoreInformation, name: String, healthcarePartyId: String): Flow<String>
	fun listPatientIdsOfHcPartyAndName(datastoreInformation: IDatastoreInformation, name: String, healthcarePartyId: String): Flow<String>
	fun listPatientIdsByHcPartyAndSsin(datastoreInformation: IDatastoreInformation, ssin: String, healthcarePartyId: String): Flow<String>
	fun listPatientIdsOfHcPartyAndSsin(datastoreInformation: IDatastoreInformation, ssin: String, healthcarePartyId: String): Flow<String>
	fun listPatientIdsByActive(datastoreInformation: IDatastoreInformation, active: Boolean, searchKeys: Set<String>): Flow<String>
	fun listOfMergesAfter(datastoreInformation: IDatastoreInformation, date: Long?): Flow<Patient>
	suspend fun countByHcParty(datastoreInformation: IDatastoreInformation, healthcarePartyId: String): Int
	suspend fun countOfHcParty(datastoreInformation: IDatastoreInformation, healthcarePartyId: String): Int
	fun listPatientIdsByHcParty(datastoreInformation: IDatastoreInformation, healthcarePartyId: String): Flow<String>
	fun listPatientIdsByHcPartyAndDateOfBirth(datastoreInformation: IDatastoreInformation, date: Int?, searchKeys: Set<String>): Flow<String>

	fun listPatientIdsByHcPartyAndDateOfBirth(datastoreInformation: IDatastoreInformation, startDate: Int?, endDate: Int?, healthcarePartyId: String): Flow<String>
	fun listPatientIdsByHcPartyGenderEducationProfession(datastoreInformation: IDatastoreInformation, healthcarePartyId: String, gender: Gender?, education: String?, profession: String?): Flow<String>
	fun listPatientIdsForHcPartyDateOfBirth(datastoreInformation: IDatastoreInformation, date: Int?, healthcarePartyId: String): Flow<String>
	fun listPatientIdsByHcPartyNameContainsFuzzy(datastoreInformation: IDatastoreInformation, searchString: String?, healthcarePartyId: String, limit: Int? = null): Flow<String>
	fun listPatientIdsOfHcPartyNameContainsFuzzy(datastoreInformation: IDatastoreInformation, searchString: String?, healthcarePartyId: String, limit: Int?): Flow<String>

	fun listPatientIdsByHcPartyAndExternalId(datastoreInformation: IDatastoreInformation, externalId: String?, healthcarePartyId: String): Flow<String>
	fun listPatientIdsByHcPartyAndTelecom(datastoreInformation: IDatastoreInformation, searchString: String?, healthcarePartyId: String): Flow<String>
	fun listPatientIdsByHcPartyAndAddress(datastoreInformation: IDatastoreInformation, searchString: String?, healthcarePartyId: String): Flow<String>
	fun listPatientIdsByHcPartyAndAddress(datastoreInformation: IDatastoreInformation, streetAndCity: String?, postalCode: String?, houseNumber: String?, healthcarePartyId: String): Flow<String>


	fun findPatientIdsByHcParty(datastoreInformation: IDatastoreInformation, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>
	fun findPatientsByHcPartyAndName(datastoreInformation: IDatastoreInformation, name: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
	fun findPatientsOfHcPartyAndName(datastoreInformation: IDatastoreInformation, name: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
	fun findPatientsByHcPartyAndSsin(datastoreInformation: IDatastoreInformation, ssin: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
	fun findPatientsOfHcPartyAndSsin(datastoreInformation: IDatastoreInformation, ssin: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

	fun findPatientsByHcPartyModificationDate(datastoreInformation: IDatastoreInformation, startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

	fun findPatientsOfHcPartyModificationDate(datastoreInformation: IDatastoreInformation, startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

	fun findPatientsByHcPartyDateOfBirth(datastoreInformation: IDatastoreInformation, startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
	fun findPatientsOfHcPartyDateOfBirth(datastoreInformation: IDatastoreInformation, startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

	suspend fun findPatientsByUserId(datastoreInformation: IDatastoreInformation, id: String): Patient?
	fun getPatients(datastoreInformation: IDatastoreInformation, patIds: Collection<String>): Flow<Patient>

	suspend fun getPatientByExternalId(datastoreInformation: IDatastoreInformation, externalId: String): Patient?

	fun findDeletedPatientsByDeleteDate(datastoreInformation: IDatastoreInformation, start: Long, end: Long?, descending: Boolean, paginationOffset: PaginationOffset<Long>): Flow<ViewQueryResultEvent>

	fun findDeletedPatientsByNames(datastoreInformation: IDatastoreInformation, firstName: String?, lastName: String?): Flow<Patient>

	fun listConflicts(datastoreInformation: IDatastoreInformation): Flow<Patient>

	fun findPatientsModifiedAfter(datastoreInformation: IDatastoreInformation, date: Long, paginationOffset: PaginationOffset<Long>): Flow<ViewQueryResultEvent>

	fun listPatientIdsByHcPartyAndSsins(datastoreInformation: IDatastoreInformation, ssins: Collection<String>, healthcarePartyId: String): Flow<String>

	@Deprecated(message = "A Data Owner may now have multiple AES Keys. Use getAesExchangeKeysForDelegate instead")
	suspend fun getHcPartyKeysForDelegate(datastoreInformation: IDatastoreInformation, healthcarePartyId: String): Map<String, String>

	suspend fun getAesExchangeKeysForDelegate(datastoreInformation: IDatastoreInformation, healthcarePartyId: String): Map<String, Map<String, Map<String, String>>>

	fun getDuplicatePatientsBySsin(datastoreInformation: IDatastoreInformation, healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>

	fun getDuplicatePatientsByName(datastoreInformation: IDatastoreInformation, healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>

	fun findPatients(datastoreInformation: IDatastoreInformation, ids: Collection<String>): Flow<ViewQueryResultEvent>

	fun findPatients(datastoreInformation: IDatastoreInformation, ids: Flow<String>): Flow<ViewQueryResultEvent>

	fun listPatientIdsByHcPartyAndIdentifiers(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, identifiers: List<Identifier>): Flow<String>

	fun listPatientsByHcPartyAndIdentifier(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, system: String, id: String): Flow<Patient>
}
