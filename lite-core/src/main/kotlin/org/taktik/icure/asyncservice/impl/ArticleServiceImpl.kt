package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.ArticleLogic
import org.taktik.icure.asyncservice.ArticleService
import org.taktik.icure.entities.Article
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult

@Service
class ArticleServiceImpl(
    private val articleLogic: ArticleLogic
) : ArticleService {
    override suspend fun createArticle(article: Article): Article? = articleLogic.createArticle(article)

    override fun deleteArticles(ids: List<String>): Flow<DocIdentifier> = articleLogic.deleteArticles(ids)

    override suspend fun deleteArticle(articleId: String): DocIdentifier = articleLogic.deleteArticles(listOf(articleId)).single()

    override suspend fun getArticle(articleId: String): Article? = articleLogic.getArticle(articleId)

    override fun getAllArticles(): Flow<Article> = articleLogic.getArticles()

    override suspend fun modifyArticle(article: Article): Article? = articleLogic.modifyArticle(article)

    override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<Article>> = articleLogic.bulkShareOrUpdateMetadata(requests)
}