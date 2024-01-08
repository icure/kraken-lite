/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.TimeTableHour
import org.taktik.icure.services.external.rest.v1.dto.embed.TimeTableHourDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface TimeTableHourMapper {
	fun map(timeTableHourDto: TimeTableHourDto): TimeTableHour
	fun map(timeTableHour: TimeTableHour): TimeTableHourDto
}
