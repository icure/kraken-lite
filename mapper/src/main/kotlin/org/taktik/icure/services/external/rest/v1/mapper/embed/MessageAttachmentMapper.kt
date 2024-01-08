/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.MessageAttachment
import org.taktik.icure.services.external.rest.v1.dto.embed.MessageAttachmentDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MessageAttachmentMapper {
	fun map(messageReadStatusDto: MessageAttachmentDto): MessageAttachment
	fun map(messageReadStatus: MessageAttachment): MessageAttachmentDto
}
