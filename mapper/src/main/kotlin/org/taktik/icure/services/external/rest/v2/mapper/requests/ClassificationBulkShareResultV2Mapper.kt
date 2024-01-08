package org.taktik.icure.services.external.rest.v2.mapper.requests

import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import org.taktik.icure.entities.Classification
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.services.external.rest.v2.dto.ClassificationDto
import org.taktik.icure.services.external.rest.v2.dto.requests.EntityBulkShareResultDto
import org.taktik.icure.services.external.rest.v2.mapper.ClassificationV2Mapper

//TODO tmp no support yet for generics

//@Mapper(
//    componentModel = "spring",
//    uses = [RejectedShareRequestV2Mapper::class],
//    injectionStrategy = InjectionStrategy.CONSTRUCTOR
//)
interface ClassificationBulkShareResultV2Mapper {
    companion object {
        private val classificationV2Mapper = Mappers.getMapper(ClassificationV2Mapper::class.java)
    }

    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["classificationToDto"])
    fun map(bulkShareResultDto: EntityBulkShareResultDto<ClassificationDto>): EntityBulkShareResult<Classification>
    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["dtoToClassification"])
    fun map(bulkShareResult: EntityBulkShareResult<Classification>): EntityBulkShareResultDto<ClassificationDto>

    @Named("classificationToDto")
    fun classificationToDto(classification: Classification?): ClassificationDto? = classification?.let { classificationV2Mapper.map(it) }

    @Named("dtoToClassification")
    fun dtoToClassification(classificationDto: ClassificationDto?): Classification? = classificationDto?.let { classificationV2Mapper.map(it) }
}

@Service
class ClassificationBulkShareResultV2MapperImpl(
    private val rejectedShareRequestV2Mapper: RejectedShareRequestV2Mapper,
    private val classificationMapper: ClassificationV2Mapper
) : ClassificationBulkShareResultV2Mapper {
    override fun map(bulkShareResultDto: EntityBulkShareResultDto<ClassificationDto>):
            EntityBulkShareResult<Classification> = EntityBulkShareResult(
        updatedEntity = bulkShareResultDto.updatedEntity?.let { classificationMapper.map(it) },
        entityId = bulkShareResultDto.entityId,
        entityRev = bulkShareResultDto.entityRev,
        rejectedRequests = bulkShareResultDto.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )

    override fun map(bulkShareResult: EntityBulkShareResult<Classification>):
            EntityBulkShareResultDto<ClassificationDto> = EntityBulkShareResultDto(
        updatedEntity =
        bulkShareResult.updatedEntity?.let { classificationMapper.map(it) },
        entityId = bulkShareResult.entityId,
        entityRev = bulkShareResult.entityRev,
        rejectedRequests = bulkShareResult.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )
}

