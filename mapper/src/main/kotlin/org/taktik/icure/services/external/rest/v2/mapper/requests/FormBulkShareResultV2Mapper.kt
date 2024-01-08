package org.taktik.icure.services.external.rest.v2.mapper.requests

import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.services.external.rest.v2.dto.FormDto
import org.taktik.icure.services.external.rest.v2.dto.requests.EntityBulkShareResultDto
import org.taktik.icure.services.external.rest.v2.mapper.FormV2Mapper

//TODO tmp no support yet for generics

//@Mapper(
//    componentModel = "spring",
//    uses = [RejectedShareRequestV2Mapper::class],
//    injectionStrategy = InjectionStrategy.CONSTRUCTOR
//)
interface FormBulkShareResultV2Mapper {
    companion object {
        private val formV2Mapper = Mappers.getMapper(FormV2Mapper::class.java)
    }

    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["formToDto"])
    fun map(bulkShareResultDto: EntityBulkShareResultDto<FormDto>): EntityBulkShareResult<Form>
    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["dtoToForm"])
    fun map(bulkShareResult: EntityBulkShareResult<Form>): EntityBulkShareResultDto<FormDto>

    @Named("formToDto")
    fun formToDto(form: Form?): FormDto? = form?.let { formV2Mapper.map(it) }

    @Named("dtoToForm")
    fun dtoToForm(formDto: FormDto?): Form? = formDto?.let { formV2Mapper.map(it) }
}

@Service
class FormBulkShareResultV2MapperImpl(
    private val rejectedShareRequestV2Mapper: RejectedShareRequestV2Mapper,
    private val formMapper: FormV2Mapper
) : FormBulkShareResultV2Mapper {
    override fun map(bulkShareResultDto: EntityBulkShareResultDto<FormDto>):
            EntityBulkShareResult<Form> = EntityBulkShareResult(
        updatedEntity = bulkShareResultDto.updatedEntity?.let { formMapper.map(it) },
        entityId = bulkShareResultDto.entityId,
        entityRev = bulkShareResultDto.entityRev,
        rejectedRequests = bulkShareResultDto.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )

    override fun map(bulkShareResult: EntityBulkShareResult<Form>):
            EntityBulkShareResultDto<FormDto> = EntityBulkShareResultDto(
        updatedEntity =
        bulkShareResult.updatedEntity?.let { formMapper.map(it) },
        entityId = bulkShareResult.entityId,
        entityRev = bulkShareResult.entityRev,
        rejectedRequests = bulkShareResult.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )
}

