/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.asynclogic.impl.filter.service

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toCollection
import org.springframework.context.annotation.Profile
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.service.ServiceByHcPartyTagCodeDateFilter
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.utils.getLoggedHealthCarePartyId

@org.springframework.stereotype.Service
@Profile("app")
class ServiceByHcPartyTagCodeDateFilter(
	private val contactLogic: ContactLogic,
	private val sessionLogic: SessionInformationProvider
) : Filter<String, Service, ServiceByHcPartyTagCodeDateFilter> {
	override fun resolve(
        filter: ServiceByHcPartyTagCodeDateFilter,
        context: Filters,
        datastoreInformation: IDatastoreInformation?
    ) = flow {
		try {
			val hcPartyId = filter.healthcarePartyId ?: getLoggedHealthCarePartyId(sessionLogic)
			var ids: LinkedHashSet<String>? = null
			val patientSFK = filter.patientSecretForeignKey
			val patientSFKList = if (patientSFK != null) listOf(patientSFK) else null
			if (filter.tagType != null && filter.tagCode != null) {
				ids = contactLogic.listServiceIdsByTag(
					hcPartyId,
					patientSFKList, filter.tagType!!,
					filter.tagCode!!, filter.startValueDate, filter.endValueDate, filter.descending
				).toCollection(LinkedHashSet())
			}
			if (filter.codeType != null && filter.codeCode != null) {
				val byCode = contactLogic.listServiceIdsByCode(
					hcPartyId,
					patientSFKList, filter.codeType!!,
					filter.codeCode!!, filter.startValueDate, filter.endValueDate, filter.descending
				).toCollection(LinkedHashSet())
				if (ids == null) {
					ids = byCode
				} else {
					ids.retainAll(byCode)
				}
			}
			emitAll(ids?.asFlow() ?: emptyFlow())
		} catch (e: LoginException) {
			throw IllegalArgumentException(e)
		}
	}
}
