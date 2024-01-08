/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asynclogic.impl.filter.healthelement

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toSet
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.healthelement.HealthElementByHcPartyTagCodeFilter
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.utils.getLoggedHealthCarePartyId

@Service
@Profile("app")
class HealthElementByHcPartyTagCodeFilter(
	private val healthElementLogic: HealthElementLogic,
	private val sessionLogic: SessionInformationProvider
) : Filter<String, HealthElement, HealthElementByHcPartyTagCodeFilter> {

	override fun resolve(
        filter: HealthElementByHcPartyTagCodeFilter,
        context: Filters,
        datastoreInformation: IDatastoreInformation?
    ) = flow {
		try {
			val hcPartyId = filter.healthcarePartyId ?: getLoggedHealthCarePartyId(sessionLogic)
			val ids = mutableSetOf<String>()
			if (filter.tagType != null && filter.tagCode != null) {
				ids.addAll(healthElementLogic.listHealthElementIdsByHcPartyAndTags(hcPartyId, filter.tagType!!, filter.tagCode!!).toSet())
			}
			if (filter.codeType != null && filter.codeCode != null) {
				val byCode = healthElementLogic.listHealthElementIdsByHcPartyAndCodes(hcPartyId, filter.codeType!!, filter.codeCode!!).toSet()
				if (ids.isEmpty()) {
					ids.addAll(byCode)
				} else {
					ids.retainAll(byCode)
				}
			}
			if (filter.status != null) {
				val byStatus = healthElementLogic.listHealthElementIdsByHcPartyAndStatus(hcPartyId, filter.status!!).toSet()
				if (ids.isEmpty()) {
					ids.addAll(byStatus)
				} else {
					ids.retainAll(byStatus)
				}
			}
			ids.forEach { emit(it) }
		} catch (e: LoginException) {
			throw IllegalArgumentException(e)
		}
	}
}
