/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.base.EntityWithSecureDelegationsLogic
import org.taktik.icure.entities.Article

interface ArticleLogic : EntityPersister<Article, String>, EntityWithSecureDelegationsLogic<Article> {
	suspend fun createArticle(article: Article): Article?
	fun deleteArticles(ids: List<String>): Flow<DocIdentifier>
	fun deleteArticles(ids: Flow<String>): Flow<DocIdentifier>
	suspend fun getArticle(articleId: String): Article?
	fun getArticles(): Flow<Article>
	suspend fun modifyArticle(article: Article): Article?
}
