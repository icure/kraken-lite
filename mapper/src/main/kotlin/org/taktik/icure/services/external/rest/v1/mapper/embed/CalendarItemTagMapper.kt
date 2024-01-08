/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.CalendarItemTag
import org.taktik.icure.services.external.rest.v1.dto.embed.CalendarItemTagDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface CalendarItemTagMapper {
	fun map(calendarItemTagDto: CalendarItemTagDto): CalendarItemTag
	fun map(calendarItemTag: CalendarItemTag): CalendarItemTagDto
}
