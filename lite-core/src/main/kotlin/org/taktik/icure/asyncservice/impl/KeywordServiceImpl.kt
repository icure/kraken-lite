package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.KeywordLogic
import org.taktik.icure.asyncservice.KeywordService
import org.taktik.icure.entities.Keyword

@Service
class KeywordServiceImpl(
    private val keywordLogic: KeywordLogic
) : KeywordService {
    override suspend fun createKeyword(keyword: Keyword): Keyword? = keywordLogic.createKeyword(keyword)

    override fun getAllKeywords(): Flow<Keyword> = keywordLogic.getEntities()

    override suspend fun getKeyword(keywordId: String): Keyword? = keywordLogic.getEntity(keywordId)

    override fun deleteKeywords(ids: Set<String>): Flow<DocIdentifier> = keywordLogic.deleteEntities(ids)

    override suspend fun modifyKeyword(keyword: Keyword): Keyword? = keywordLogic.modifyKeyword(keyword)

    override fun getKeywordsByUser(userId: String): Flow<Keyword> = keywordLogic.getKeywordsByUser(userId)
}
