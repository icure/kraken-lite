package org.taktik.icure.services.external.rest.v2.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Annotation
import org.taktik.icure.services.external.rest.v2.dto.embed.AnnotationDto
import org.taktik.icure.services.external.rest.v2.mapper.base.CodeStubV2Mapper

@Mapper(componentModel = "spring", uses = [CodeStubV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AnnotationV2Mapper {
	fun map(annotationDto: AnnotationDto): Annotation
	fun map(annotation: Annotation): AnnotationDto
}
