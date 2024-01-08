/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.FlowItem
import org.taktik.icure.services.external.rest.v1.dto.embed.FlowItemDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface FlowItemMapper {
	fun map(flowItemDto: FlowItemDto): FlowItem
	fun map(flowItem: FlowItem): FlowItemDto
}
