/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.MessagesReadStatusUpdate
import org.taktik.icure.services.external.rest.v1.dto.embed.MessagesReadStatusUpdateDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MessagesReadStatusUpdateMapper {
	fun map(messagesReadStatusUpdateDto: MessagesReadStatusUpdateDto): MessagesReadStatusUpdate
	fun map(messagesReadStatusUpdate: MessagesReadStatusUpdate): MessagesReadStatusUpdateDto
}
