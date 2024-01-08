/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Suspension
import org.taktik.icure.services.external.rest.v1.dto.embed.SuspensionDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface SuspensionMapper {
	fun map(suspensionDto: SuspensionDto): Suspension
	fun map(suspension: Suspension): SuspensionDto
}
