/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.TimeSeries
import org.taktik.icure.services.external.rest.v1.dto.embed.TimeSeriesDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface TimeSeriesMapper {
	fun map(measureDto: TimeSeriesDto): TimeSeries
	fun map(measure: TimeSeries): TimeSeriesDto
}
