package org.taktik.icure.asynclogic.impl.filter.healthelement

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.healthelement.HealthElementByHcPartyIdentifiersFilter
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.utils.getLoggedHealthCarePartyId

@Service
@Profile("app")
class HealthElementByHcPartyIdentifiersFilter(
	private val healthElementLogic: HealthElementLogic,
	private val sessionLogic: SessionInformationProvider
) : Filter<String, HealthElement, HealthElementByHcPartyIdentifiersFilter> {
	override fun resolve(
        filter: HealthElementByHcPartyIdentifiersFilter,
        context: Filters,
        datastoreInformation: IDatastoreInformation?
    ) = flow {
		try {
			val hcPartyId: String = filter.hcPartyId ?: getLoggedHealthCarePartyId(sessionLogic)
			emitAll(healthElementLogic.listHealthElementsIdsByHcPartyAndIdentifiers(hcPartyId, filter.identifiers))
		} catch (e: LoginException) {
			throw IllegalArgumentException(e)
		}
	}
}
