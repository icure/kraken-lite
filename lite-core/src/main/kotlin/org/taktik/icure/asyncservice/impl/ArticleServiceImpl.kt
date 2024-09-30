package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.ArticleLogic
import org.taktik.icure.asyncservice.ArticleService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Article
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.pagination.PaginationElement

@Service
class ArticleServiceImpl(
    private val articleLogic: ArticleLogic
) : ArticleService {
    override suspend fun createArticle(article: Article): Article? = articleLogic.createArticle(article)

    override suspend fun getArticle(articleId: String): Article? = articleLogic.getArticle(articleId)
    override fun getAllArticles(paginationOffset: PaginationOffset<Nothing>): Flow<PaginationElement> = articleLogic.getAllArticles(paginationOffset)
    override fun getAllArticles(): Flow<Article> = articleLogic.getEntities()
    override suspend fun modifyArticle(article: Article): Article? = articleLogic.modifyArticle(article)
    override fun deleteArticles(ids: List<IdAndRev>): Flow<DocIdentifier> = articleLogic.deleteEntities(ids)
    override suspend fun deleteArticle(id: String, rev: String?): DocIdentifier = articleLogic.deleteEntity(id, rev)
    override suspend fun purgeArticle(id: String, rev: String): DocIdentifier = articleLogic.purgeEntity(id, rev)
    override suspend fun undeleteArticle(id: String, rev: String): Article = articleLogic.undeleteEntity(id, rev)

    override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<Article>> = articleLogic.bulkShareOrUpdateMetadata(requests)
}