/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.ReferralPeriod
import org.taktik.icure.services.external.rest.v1.dto.embed.ReferralPeriodDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ReferralPeriodMapper {
	fun map(referralPeriodDto: ReferralPeriodDto): ReferralPeriod
	fun map(referralPeriod: ReferralPeriod): ReferralPeriodDto
}
