/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PlannedSending
import org.taktik.icure.services.external.rest.v1.dto.embed.PlannedSendingDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PlannedSendingMapper {
	fun map(plannedSendingDto: PlannedSendingDto): PlannedSending
	fun map(plannedSending: PlannedSending): PlannedSendingDto
}
