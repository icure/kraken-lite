/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Right
import org.taktik.icure.services.external.rest.v1.dto.embed.RightDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface RightMapper {
	fun map(rightDto: RightDto): Right
	fun map(right: Right): RightDto
}
