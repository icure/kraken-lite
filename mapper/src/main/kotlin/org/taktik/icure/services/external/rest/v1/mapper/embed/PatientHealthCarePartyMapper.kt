/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PatientHealthCareParty
import org.taktik.icure.services.external.rest.v1.dto.embed.PatientHealthCarePartyDto

@Mapper(componentModel = "spring", uses = [ReferralPeriodMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PatientHealthCarePartyMapper {
	fun map(patientHealthCarePartyDto: PatientHealthCarePartyDto): PatientHealthCareParty
	fun map(patientHealthCareParty: PatientHealthCareParty): PatientHealthCarePartyDto
}
