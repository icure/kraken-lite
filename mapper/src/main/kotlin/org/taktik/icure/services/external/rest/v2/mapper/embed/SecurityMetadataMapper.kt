package org.taktik.icure.services.external.rest.v2.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.SecurityMetadata
import org.taktik.icure.services.external.rest.v2.dto.embed.SecurityMetadataDto

@Mapper(componentModel = "spring", uses = [SecureDelegationV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface SecurityMetadataV2Mapper {
	fun map(securityMetadataDto: SecurityMetadataDto): SecurityMetadata

	fun map(securityMetadata: SecurityMetadata): SecurityMetadataDto
}
