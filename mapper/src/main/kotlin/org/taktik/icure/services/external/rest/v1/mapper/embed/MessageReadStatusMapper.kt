/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.MessageReadStatus
import org.taktik.icure.services.external.rest.v1.dto.embed.MessageReadStatusDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MessageReadStatusMapper {
	fun map(messageReadStatusDto: MessageReadStatusDto): MessageReadStatus
	fun map(messageReadStatus: MessageReadStatus): MessageReadStatusDto
}
