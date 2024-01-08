/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.AgreementAppendix
import org.taktik.icure.services.external.rest.v1.dto.embed.AgreementAppendixDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AgreementAppendixMapper {
	fun map(agreementAppendixDto: AgreementAppendixDto): AgreementAppendix
	fun map(agreementAppendix: AgreementAppendix): AgreementAppendixDto
}
