/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Country
import org.taktik.icure.services.external.rest.v1.dto.embed.CountryDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface CountryMapper {
	fun map(countryDto: CountryDto): Country
	fun map(country: Country): CountryDto
}
