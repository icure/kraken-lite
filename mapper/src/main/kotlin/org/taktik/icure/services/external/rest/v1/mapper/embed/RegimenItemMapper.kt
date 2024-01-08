/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.AdministrationQuantity
import org.taktik.icure.entities.embed.RegimenItem
import org.taktik.icure.entities.embed.Weekday
import org.taktik.icure.services.external.rest.v1.dto.embed.RegimenItemDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface RegimenItemMapper {
	fun map(regimenItemDto: RegimenItemDto): RegimenItem
	fun map(regimenItem: RegimenItem): RegimenItemDto
	fun map(weekday: Weekday): org.taktik.icure.services.external.rest.v1.dto.embed.Weekday
	fun map(weekday: org.taktik.icure.services.external.rest.v1.dto.embed.Weekday): Weekday
	fun map(administrationQuantity: AdministrationQuantity): org.taktik.icure.services.external.rest.v1.dto.embed.AdministrationQuantity
	fun map(administrationQuantity: org.taktik.icure.services.external.rest.v1.dto.embed.AdministrationQuantity): AdministrationQuantity
}
