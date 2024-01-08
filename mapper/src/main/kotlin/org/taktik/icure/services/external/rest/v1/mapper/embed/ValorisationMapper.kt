/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Valorisation
import org.taktik.icure.services.external.rest.v1.dto.embed.ValorisationDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ValorisationMapper {
	fun map(valorisationDto: ValorisationDto): Valorisation
	fun map(valorisation: Valorisation): ValorisationDto
}
