package org.taktik.icure.services.external.rest.v2.mapper.requests

import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.services.external.rest.v2.dto.MessageDto
import org.taktik.icure.services.external.rest.v2.dto.requests.EntityBulkShareResultDto
import org.taktik.icure.services.external.rest.v2.mapper.MessageV2Mapper

//TODO tmp no support yet for generics

//@Mapper(
//    componentModel = "spring",
//    uses = [RejectedShareRequestV2Mapper::class],
//    injectionStrategy = InjectionStrategy.CONSTRUCTOR
//)
interface MessageBulkShareResultV2Mapper {
    companion object {
        private val messageV2Mapper = Mappers.getMapper(MessageV2Mapper::class.java)
    }

    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["messageToDto"])
    fun map(bulkShareResultDto: EntityBulkShareResultDto<MessageDto>): EntityBulkShareResult<Message>
    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["dtoToMessage"])
    fun map(bulkShareResult: EntityBulkShareResult<Message>): EntityBulkShareResultDto<MessageDto>

    @Named("messageToDto")
    fun messageToDto(message: Message?): MessageDto? = message?.let { messageV2Mapper.map(it) }

    @Named("dtoToMessage")
    fun dtoToMessage(messageDto: MessageDto?): Message? = messageDto?.let { messageV2Mapper.map(it) }
}

@Service
class MessageBulkShareResultV2MapperImpl(
    private val rejectedShareRequestV2Mapper: RejectedShareRequestV2Mapper,
    private val messageMapper: MessageV2Mapper
) : MessageBulkShareResultV2Mapper {
    override fun map(bulkShareResultDto: EntityBulkShareResultDto<MessageDto>):
            EntityBulkShareResult<Message> = EntityBulkShareResult(
        updatedEntity = bulkShareResultDto.updatedEntity?.let { messageMapper.map(it) },
        entityId = bulkShareResultDto.entityId,
        entityRev = bulkShareResultDto.entityRev,
        rejectedRequests = bulkShareResultDto.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )

    override fun map(bulkShareResult: EntityBulkShareResult<Message>):
            EntityBulkShareResultDto<MessageDto> = EntityBulkShareResultDto(
        updatedEntity =
        bulkShareResult.updatedEntity?.let { messageMapper.map(it) },
        entityId = bulkShareResult.entityId,
        entityRev = bulkShareResult.entityRev,
        rejectedRequests = bulkShareResult.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )
}

