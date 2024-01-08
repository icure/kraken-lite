/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.DatabaseSynchronization
import org.taktik.icure.services.external.rest.v1.dto.embed.DatabaseSynchronizationDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface DatabaseSynchronizationMapper {
	fun map(databaseSynchronizationDto: DatabaseSynchronizationDto): DatabaseSynchronization
	fun map(databaseSynchronization: DatabaseSynchronization): DatabaseSynchronizationDto
}
