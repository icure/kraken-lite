package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.SecurityMetadata
import org.taktik.icure.services.external.rest.v1.dto.embed.SecurityMetadataDto

@Mapper(componentModel = "spring", uses = [SecureDelegationMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface SecurityMetadataMapper {
	fun map(securityMetadataDto: SecurityMetadataDto): SecurityMetadata

	fun map(securityMetadata: SecurityMetadata): SecurityMetadataDto
}
