package org.taktik.icure.services.external.rest.v2.mapper.requests

import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.services.external.rest.v2.dto.DocumentDto
import org.taktik.icure.services.external.rest.v2.dto.requests.EntityBulkShareResultDto
import org.taktik.icure.services.external.rest.v2.mapper.DocumentV2Mapper

//TODO tmp no support yet for generics

//@Mapper(
//    componentModel = "spring",
//    uses = [RejectedShareRequestV2Mapper::class],
//    injectionStrategy = InjectionStrategy.CONSTRUCTOR
//)
interface DocumentBulkShareResultV2Mapper {
    companion object {
        private val documentV2Mapper = Mappers.getMapper(DocumentV2Mapper::class.java)
    }

    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["documentToDto"])
    fun map(bulkShareResultDto: EntityBulkShareResultDto<DocumentDto>): EntityBulkShareResult<Document>
    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["dtoToDocument"])
    fun map(bulkShareResult: EntityBulkShareResult<Document>): EntityBulkShareResultDto<DocumentDto>

    @Named("documentToDto")
    fun documentToDto(document: Document?): DocumentDto? = document?.let { documentV2Mapper.map(it) }

    @Named("dtoToDocument")
    fun dtoToDocument(documentDto: DocumentDto?): Document? = documentDto?.let { documentV2Mapper.map(it) }
}

@Service
class DocumentBulkShareResultV2MapperImpl(
    private val rejectedShareRequestV2Mapper: RejectedShareRequestV2Mapper,
    private val documentMapper: DocumentV2Mapper
) : DocumentBulkShareResultV2Mapper {
    override fun map(bulkShareResultDto: EntityBulkShareResultDto<DocumentDto>):
            EntityBulkShareResult<Document> = EntityBulkShareResult(
        updatedEntity = bulkShareResultDto.updatedEntity?.let { documentMapper.map(it) },
        entityId = bulkShareResultDto.entityId,
        entityRev = bulkShareResultDto.entityRev,
        rejectedRequests = bulkShareResultDto.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )

    override fun map(bulkShareResult: EntityBulkShareResult<Document>):
            EntityBulkShareResultDto<DocumentDto> = EntityBulkShareResultDto(
        updatedEntity =
        bulkShareResult.updatedEntity?.let { documentMapper.map(it) },
        entityId = bulkShareResult.entityId,
        entityRev = bulkShareResult.entityRev,
        rejectedRequests = bulkShareResult.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )
}

