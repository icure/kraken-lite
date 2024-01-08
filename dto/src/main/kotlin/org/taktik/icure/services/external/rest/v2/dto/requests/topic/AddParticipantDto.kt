package org.taktik.icure.services.external.rest.v2.dto.requests.topic

import org.taktik.icure.services.external.rest.v2.dto.TopicRoleDto

data class AddParticipantDto(
    val dataOwnerId: String,
    val topicRole: TopicRoleDto,
)