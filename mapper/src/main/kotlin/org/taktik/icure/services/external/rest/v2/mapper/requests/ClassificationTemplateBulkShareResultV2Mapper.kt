package org.taktik.icure.services.external.rest.v2.mapper.requests

import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import org.taktik.icure.entities.ClassificationTemplate
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.services.external.rest.v2.dto.ClassificationTemplateDto
import org.taktik.icure.services.external.rest.v2.dto.requests.EntityBulkShareResultDto
import org.taktik.icure.services.external.rest.v2.mapper.ClassificationTemplateV2Mapper

//TODO tmp no support yet for generics

//@Mapper(
//    componentModel = "spring",
//    uses = [RejectedShareRequestV2Mapper::class],
//    injectionStrategy = InjectionStrategy.CONSTRUCTOR
//)
interface ClassificationTemplateBulkShareResultV2Mapper {
    companion object {
        private val classificationV2Mapper = Mappers.getMapper(ClassificationTemplateV2Mapper::class.java)
    }

    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["classificationToDto"])
    fun map(bulkShareResultDto: EntityBulkShareResultDto<ClassificationTemplateDto>): EntityBulkShareResult<ClassificationTemplate>
    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["dtoToClassificationTemplate"])
    fun map(bulkShareResult: EntityBulkShareResult<ClassificationTemplate>): EntityBulkShareResultDto<ClassificationTemplateDto>

    @Named("classificationToDto")
    fun classificationToDto(classification: ClassificationTemplate?): ClassificationTemplateDto? = classification?.let { classificationV2Mapper.map(it) }

    @Named("dtoToClassificationTemplate")
    fun dtoToClassificationTemplate(classificationDto: ClassificationTemplateDto?): ClassificationTemplate? = classificationDto?.let { classificationV2Mapper.map(it) }
}

@Service
class ClassificationTemplateBulkShareResultV2MapperImpl(
    private val rejectedShareRequestV2Mapper: RejectedShareRequestV2Mapper,
    private val classificationMapper: ClassificationTemplateV2Mapper
) : ClassificationTemplateBulkShareResultV2Mapper {
    override fun map(bulkShareResultDto: EntityBulkShareResultDto<ClassificationTemplateDto>):
            EntityBulkShareResult<ClassificationTemplate> = EntityBulkShareResult(
        updatedEntity = bulkShareResultDto.updatedEntity?.let { classificationMapper.map(it) },
        entityId = bulkShareResultDto.entityId,
        entityRev = bulkShareResultDto.entityRev,
        rejectedRequests = bulkShareResultDto.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )

    override fun map(bulkShareResult: EntityBulkShareResult<ClassificationTemplate>):
            EntityBulkShareResultDto<ClassificationTemplateDto> = EntityBulkShareResultDto(
        updatedEntity =
        bulkShareResult.updatedEntity?.let { classificationMapper.map(it) },
        entityId = bulkShareResult.entityId,
        entityRev = bulkShareResult.entityRev,
        rejectedRequests = bulkShareResult.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )
}

