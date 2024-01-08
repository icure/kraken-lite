/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.IdentityDocumentReader
import org.taktik.icure.services.external.rest.v1.dto.embed.IdentityDocumentReaderDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface IdentityDocumentReaderMapper {
	fun map(identityDocumentReaderDto: IdentityDocumentReaderDto): IdentityDocumentReader
	fun map(identityDocumentReader: IdentityDocumentReader): IdentityDocumentReaderDto
}
