/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.services.external.rest.v1.dto.embed.RevisionInfoDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface RevisionInfoMapper {
	fun map(revisionInfoDto: RevisionInfoDto): RevisionInfo
	fun map(revisionInfo: RevisionInfo): RevisionInfoDto
}
