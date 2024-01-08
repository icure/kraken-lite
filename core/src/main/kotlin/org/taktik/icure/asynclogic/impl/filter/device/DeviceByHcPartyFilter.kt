package org.taktik.icure.asynclogic.impl.filter.device

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.asynclogic.DeviceLogic
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.device.DeviceByHcPartyFilter
import org.taktik.icure.entities.Device
import org.taktik.icure.utils.getLoggedHealthCarePartyId

@Service
@Profile("app")
class DeviceByHcPartyFilter(
	private val deviceLogic: DeviceLogic,
	private val sessionLogic: SessionInformationProvider
) : Filter<String, Device, DeviceByHcPartyFilter> {

	override fun resolve(filter: DeviceByHcPartyFilter, context: Filters, datastoreInformation: IDatastoreInformation?) = flow {
		try {
			emitAll(deviceLogic.listDeviceIdsByResponsible(filter.responsibleId ?: getLoggedHealthCarePartyId(sessionLogic)))
		} catch (e: LoginException) {
			throw IllegalArgumentException(e)
		}
	}
}
