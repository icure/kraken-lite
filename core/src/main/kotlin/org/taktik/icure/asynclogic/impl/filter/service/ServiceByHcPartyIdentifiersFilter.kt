package org.taktik.icure.asynclogic.impl.filter.service

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.context.annotation.Profile
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.service.ServiceByHcPartyIdentifiersFilter
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.utils.getLoggedHealthCarePartyId

@org.springframework.stereotype.Service
@Profile("app")
class ServiceByHcPartyIdentifiersFilter(
    private val contactLogic: ContactLogic,
    private val sessionLogic: SessionInformationProvider,
) : Filter<String, Service, ServiceByHcPartyIdentifiersFilter> {
	override fun resolve(
        filter: ServiceByHcPartyIdentifiersFilter,
        context: Filters,
        datastoreInformation: IDatastoreInformation?
    ) = flow {
		try {
			val hcPartyId = filter.healthcarePartyId ?: getLoggedHealthCarePartyId(sessionLogic)
			emitAll(contactLogic.listServiceIdsByHcPartyAndIdentifiers(hcPartyId, filter.identifiers))
		} catch (e: LoginException) {
			throw IllegalArgumentException(e)
		}
	}
}
