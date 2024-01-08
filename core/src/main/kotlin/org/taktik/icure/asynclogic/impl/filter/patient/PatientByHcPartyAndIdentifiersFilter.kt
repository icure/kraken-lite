package org.taktik.icure.asynclogic.impl.filter.patient

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.patient.PatientByHcPartyAndIdentifiersFilter
import org.taktik.icure.entities.Patient
import org.taktik.icure.utils.getLoggedHealthCarePartyId

@Service
@Profile("app")
class PatientByHcPartyAndIdentifiersFilter(private val patientLogic: PatientLogic, private val sessionLogic: SessionInformationProvider) :
    Filter<String, Patient, PatientByHcPartyAndIdentifiersFilter> {

	override fun resolve(
        filter: PatientByHcPartyAndIdentifiersFilter,
        context: Filters,
        datastoreInformation: IDatastoreInformation?
    ) = flow {
		try {
			emitAll(patientLogic.listPatientIdsByHcpartyAndIdentifiers(filter.healthcarePartyId ?: getLoggedHealthCarePartyId(sessionLogic), filter.identifiers))
		} catch (e: LoginException) {
			throw IllegalArgumentException(e)
		}
	}
}
