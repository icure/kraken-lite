package org.taktik.icure.domain.filter.message

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.Message

interface LatestMessageByHcPartyTransportGuidFilter : Filter<String, Message> {
    val healthcarePartyId: String
    val transportGuid: String
}