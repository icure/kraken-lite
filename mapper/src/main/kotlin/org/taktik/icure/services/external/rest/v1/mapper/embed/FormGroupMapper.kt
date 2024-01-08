/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.FormGroup
import org.taktik.icure.services.external.rest.v1.dto.embed.FormGroupDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface FormGroupMapper {
	fun map(formGroupDto: FormGroupDto): FormGroup
	fun map(formGroup: FormGroup): FormGroupDto
}
