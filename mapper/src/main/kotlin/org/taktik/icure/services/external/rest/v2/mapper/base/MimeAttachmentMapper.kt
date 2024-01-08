package org.taktik.icure.services.external.rest.v2.mapper.base

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.domain.result.MimeAttachment
import org.taktik.icure.services.external.rest.v2.dto.base.MimeAttachmentDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MimeAttachmentV2Mapper {
	fun map(mimeAttachmentDto: MimeAttachmentDto): MimeAttachment
	fun map(mimeAttachment: MimeAttachment): MimeAttachmentDto
}
