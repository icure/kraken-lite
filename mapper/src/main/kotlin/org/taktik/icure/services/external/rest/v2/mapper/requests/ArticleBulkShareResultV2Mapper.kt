package org.taktik.icure.services.external.rest.v2.mapper.requests

import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import org.taktik.icure.entities.Article
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.services.external.rest.v2.dto.ArticleDto
import org.taktik.icure.services.external.rest.v2.dto.requests.EntityBulkShareResultDto
import org.taktik.icure.services.external.rest.v2.mapper.ArticleV2Mapper

//TODO tmp no support yet for generics

//@Mapper(
//    componentModel = "spring",
//    uses = [RejectedShareRequestV2Mapper::class],
//    injectionStrategy = InjectionStrategy.CONSTRUCTOR
//)
interface ArticleBulkShareResultV2Mapper {
    companion object {
        private val articleV2Mapper = Mappers.getMapper(ArticleV2Mapper::class.java)
    }

    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["articleToDto"])
    fun map(bulkShareResultDto: EntityBulkShareResultDto<ArticleDto>): EntityBulkShareResult<Article>
    @Mapping(source = "updatedEntity", target = "updatedEntity", qualifiedByName = ["dtoToArticle"])
    fun map(bulkShareResult: EntityBulkShareResult<Article>): EntityBulkShareResultDto<ArticleDto>

    @Named("articleToDto")
    fun articleToDto(article: Article?): ArticleDto? = article?.let { articleV2Mapper.map(it) }

    @Named("dtoToArticle")
    fun dtoToArticle(articleDto: ArticleDto?): Article? = articleDto?.let { articleV2Mapper.map(it) }
}

@Service
class ArticleBulkShareResultV2MapperImpl(
    private val rejectedShareRequestV2Mapper: RejectedShareRequestV2Mapper,
    private val articleMapper: ArticleV2Mapper
) : ArticleBulkShareResultV2Mapper {
    override fun map(bulkShareResultDto: EntityBulkShareResultDto<ArticleDto>):
            EntityBulkShareResult<Article> = EntityBulkShareResult(
        updatedEntity = bulkShareResultDto.updatedEntity?.let { articleMapper.map(it) },
        entityId = bulkShareResultDto.entityId,
        entityRev = bulkShareResultDto.entityRev,
        rejectedRequests = bulkShareResultDto.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )

    override fun map(bulkShareResult: EntityBulkShareResult<Article>):
            EntityBulkShareResultDto<ArticleDto> = EntityBulkShareResultDto(
        updatedEntity =
        bulkShareResult.updatedEntity?.let { articleMapper.map(it) },
        entityId = bulkShareResult.entityId,
        entityRev = bulkShareResult.entityRev,
        rejectedRequests = bulkShareResult.rejectedRequests.map { (k, v) ->
            k to this.rejectedShareRequestV2Mapper.map(v)
        }.toMap(),
    )
}

