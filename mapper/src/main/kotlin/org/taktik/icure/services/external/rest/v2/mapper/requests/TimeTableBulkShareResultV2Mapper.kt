package org.taktik.icure.services.external.rest.v2.mapper.requests

import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import org.taktik.icure.entities.TimeTable
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.services.external.rest.v2.dto.TimeTableDto
import org.taktik.icure.services.external.rest.v2.dto.requests.EntityBulkShareResultDto
import org.taktik.icure.services.external.rest.v2.mapper.TimeTableV2Mapper

//TODO tmp no support yet for generics

//@Mapper(
//    componentModel = "spring",
//    uses = [RejectedShareRequestV2Mapper::class],
//    injectionStrategy = InjectionStrategy.CONSTRUCTOR
//)
interface TimeTableBulkShareResultV2Mapper {
    companion object {
        private val timeTableV2Mapper = Mappers.getMapper(TimeTableV2Mapper::class.java)
    }

    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["timeTableToDto"])
    fun map(bulkShareResultDto: EntityBulkShareResultDto<TimeTableDto>): EntityBulkShareResult<TimeTable>
    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["dtoToTimeTable"])
    fun map(bulkShareResult: EntityBulkShareResult<TimeTable>): EntityBulkShareResultDto<TimeTableDto>

    @Named("timeTableToDto")
    fun timeTableToDto(timeTable: TimeTable?): TimeTableDto? = timeTable?.let { timeTableV2Mapper.map(it) }

    @Named("dtoToTimeTable")
    fun dtoToTimeTable(timeTableDto: TimeTableDto?): TimeTable? = timeTableDto?.let { timeTableV2Mapper.map(it) }
}

@Service
class TimeTableBulkShareResultV2MapperImpl(
    private val rejectedShareRequestV2Mapper: RejectedShareRequestV2Mapper,
    private val timeTableMapper: TimeTableV2Mapper
) : TimeTableBulkShareResultV2Mapper {
    override fun map(bulkShareResultDto: EntityBulkShareResultDto<TimeTableDto>):
            EntityBulkShareResult<TimeTable> = EntityBulkShareResult(
        updatedEntity = bulkShareResultDto.updatedEntity?.let { timeTableMapper.map(it) },
        entityId = bulkShareResultDto.entityId,
        entityRev = bulkShareResultDto.entityRev,
        rejectedRequests = bulkShareResultDto.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )

    override fun map(bulkShareResult: EntityBulkShareResult<TimeTable>):
            EntityBulkShareResultDto<TimeTableDto> = EntityBulkShareResultDto(
        updatedEntity =
        bulkShareResult.updatedEntity?.let { timeTableMapper.map(it) },
        entityId = bulkShareResult.entityId,
        entityRev = bulkShareResult.entityRev,
        rejectedRequests = bulkShareResult.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )
}

