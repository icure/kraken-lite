/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.domain.filter.impl.service

import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.base.Encryptable
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.entities.embed.asEncryptable

data class ServiceByHcPartyIdentifiersFilter(
	override val desc: String? = null,
	override val healthcarePartyId: String? = null,
	override val identifiers: List<Identifier> = emptyList(),
) : AbstractFilter<Service>, org.taktik.icure.domain.filter.service.ServiceByHcPartyIdentifiersFilter {

	override val requiresSecurityPrecondition: Boolean = false
	override fun requestedDataOwnerIds(): Set<String> = healthcarePartyId?.let { setOf(it) } ?: emptySet()

	override fun matches(item: Service, searchKeyMatcher: (String, Encryptable) -> Boolean): Boolean {
		return (
			item.endOfLife == null && (healthcarePartyId == null || item.asEncryptable()?.let { searchKeyMatcher(healthcarePartyId, it) } == true) &&
				identifiers.any { searchIdentifier -> item.identifier.any { it.system == searchIdentifier.system && it.id == searchIdentifier.id } }
			)
	}
}
