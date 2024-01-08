/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.asynclogic.impl.filter.contact

import kotlinx.coroutines.flow.*
import javax.security.auth.login.LoginException
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.contact.ContactByHcPartyTagCodeDateFilter
import org.taktik.icure.entities.Contact
import org.taktik.icure.utils.getLoggedHealthCarePartyId

@Service
@Profile("app")
class ContactByHcPartyTagCodeDateFilter(
	private val contactLogic: ContactLogic,
	private val sessionLogic: SessionInformationProvider
) : Filter<String, Contact, ContactByHcPartyTagCodeDateFilter> {

	override fun resolve(
        filter: ContactByHcPartyTagCodeDateFilter,
        context: Filters,
        datastoreInformation: IDatastoreInformation?
    ) = flow {
		try {
			val hcPartyId: String = filter.healthcarePartyId ?: getLoggedHealthCarePartyId(sessionLogic)
			val ids = mutableSetOf<String>()
			if (filter.tagType != null && filter.tagCode != null) {
				ids.addAll(contactLogic.listContactIdsByTag(
						hcPartyId,
						filter.tagType!!,
						filter.tagCode!!,
						filter.startOfContactOpeningDate, filter.endOfContactOpeningDate
					).toSet())
			}
			if (filter.codeType != null && filter.codeCode != null) {
				val byCode = contactLogic.listContactIdsByCode(
					hcPartyId,
					filter.codeType!!,
					filter.codeCode!!,
					filter.startOfContactOpeningDate, filter.endOfContactOpeningDate
				).toSet()
				if (ids.isEmpty()) {
					ids.addAll(byCode)
				} else {
					ids.retainAll(byCode)
				}
			}
			emitAll(ids.asFlow())
		} catch (e: LoginException) {
			throw IllegalArgumentException(e)
		}
	}
}
