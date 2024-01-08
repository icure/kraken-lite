/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Partnership
import org.taktik.icure.services.external.rest.v1.dto.embed.PartnershipDto

@Mapper(componentModel = "spring", uses = [], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PartnershipMapper {
	fun map(partnershipDto: PartnershipDto): Partnership
	fun map(partnership: Partnership): PartnershipDto
}
