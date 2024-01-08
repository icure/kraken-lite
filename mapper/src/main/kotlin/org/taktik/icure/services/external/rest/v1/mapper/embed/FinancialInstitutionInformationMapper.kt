/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.FinancialInstitutionInformation
import org.taktik.icure.services.external.rest.v1.dto.embed.FinancialInstitutionInformationDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface FinancialInstitutionInformationMapper {
	fun map(financialInstitutionInformationDto: FinancialInstitutionInformationDto): FinancialInstitutionInformation
	fun map(financialInstitutionInformation: FinancialInstitutionInformation): FinancialInstitutionInformationDto
}
