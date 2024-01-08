package org.taktik.icure.services.external.rest.v2.mapper.requests

import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import org.taktik.icure.entities.Receipt
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.services.external.rest.v2.dto.ReceiptDto
import org.taktik.icure.services.external.rest.v2.dto.requests.EntityBulkShareResultDto
import org.taktik.icure.services.external.rest.v2.mapper.ReceiptV2Mapper

//TODO tmp no support yet for generics

//@Mapper(
//    componentModel = "spring",
//    uses = [RejectedShareRequestV2Mapper::class],
//    injectionStrategy = InjectionStrategy.CONSTRUCTOR
//)
interface ReceiptBulkShareResultV2Mapper {
    companion object {
        private val receiptV2Mapper = Mappers.getMapper(ReceiptV2Mapper::class.java)
    }

    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["receiptToDto"])
    fun map(bulkShareResultDto: EntityBulkShareResultDto<ReceiptDto>): EntityBulkShareResult<Receipt>
    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["dtoToReceipt"])
    fun map(bulkShareResult: EntityBulkShareResult<Receipt>): EntityBulkShareResultDto<ReceiptDto>

    @Named("receiptToDto")
    fun receiptToDto(receipt: Receipt?): ReceiptDto? = receipt?.let { receiptV2Mapper.map(it) }

    @Named("dtoToReceipt")
    fun dtoToReceipt(receiptDto: ReceiptDto?): Receipt? = receiptDto?.let { receiptV2Mapper.map(it) }
}

@Service
class ReceiptBulkShareResultV2MapperImpl(
    private val rejectedShareRequestV2Mapper: RejectedShareRequestV2Mapper,
    private val receiptMapper: ReceiptV2Mapper
) : ReceiptBulkShareResultV2Mapper {
    override fun map(bulkShareResultDto: EntityBulkShareResultDto<ReceiptDto>):
            EntityBulkShareResult<Receipt> = EntityBulkShareResult(
        updatedEntity = bulkShareResultDto.updatedEntity?.let { receiptMapper.map(it) },
        entityId = bulkShareResultDto.entityId,
        entityRev = bulkShareResultDto.entityRev,
        rejectedRequests = bulkShareResultDto.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )

    override fun map(bulkShareResult: EntityBulkShareResult<Receipt>):
            EntityBulkShareResultDto<ReceiptDto> = EntityBulkShareResultDto(
        updatedEntity =
        bulkShareResult.updatedEntity?.let { receiptMapper.map(it) },
        entityId = bulkShareResult.entityId,
        entityRev = bulkShareResult.entityRev,
        rejectedRequests = bulkShareResult.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )
}

