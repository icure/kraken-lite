/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PostalCode
import org.taktik.icure.services.external.rest.v1.dto.embed.PostalCodeDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PostalCodeMapper {
	fun map(postalCodeDto: PostalCodeDto): PostalCode
	fun map(postalCode: PostalCode): PostalCodeDto
}
