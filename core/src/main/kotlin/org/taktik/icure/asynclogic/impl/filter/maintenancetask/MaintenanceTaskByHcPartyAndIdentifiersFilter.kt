package org.taktik.icure.asynclogic.impl.filter.maintenancetask

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.asynclogic.MaintenanceTaskLogic
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.maintenancetask.MaintenanceTaskByHcPartyAndIdentifiersFilter
import org.taktik.icure.entities.MaintenanceTask
import org.taktik.icure.utils.getLoggedDataOwnerId

@Service
@Profile("app")
class MaintenanceTaskByHcPartyAndIdentifiersFilter(private val maintenanceTaskLogic: MaintenanceTaskLogic, private val sessionLogic: SessionInformationProvider) :
    Filter<String, MaintenanceTask, MaintenanceTaskByHcPartyAndIdentifiersFilter> {

	override fun resolve(
        filter: MaintenanceTaskByHcPartyAndIdentifiersFilter,
        context: Filters,
        datastoreInformation: IDatastoreInformation?
    ) = flow {
		try {
			emitAll(maintenanceTaskLogic.listMaintenanceTasksByHcPartyAndIdentifier(filter.healthcarePartyId ?: getLoggedDataOwnerId(sessionLogic), filter.identifiers))
		} catch (e: LoginException) {
			throw IllegalArgumentException(e)
		}
	}
}
