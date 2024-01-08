/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Insurability
import org.taktik.icure.services.external.rest.v1.dto.embed.InsurabilityDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface InsurabilityMapper {
	fun map(insurabilityDto: InsurabilityDto): Insurability
	fun map(insurability: Insurability): InsurabilityDto
}
