/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.domain.result.ResultInfo
import org.taktik.icure.services.external.rest.v1.dto.ResultInfoDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeMapper
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.ServiceMapper

@Mapper(componentModel = "spring", uses = [DelegationMapper::class, ServiceMapper::class, CodeMapper::class, CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ResultInfoMapper {
	fun map(resultInfoDto: ResultInfoDto): ResultInfo
	fun map(resultInfo: ResultInfo): ResultInfoDto
}
