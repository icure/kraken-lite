/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.entities.Keyword

interface KeywordLogic : EntityPersister<Keyword, String> {
	suspend fun createKeyword(keyword: Keyword): Keyword?

	suspend fun getKeyword(keywordId: String): Keyword?
	fun deleteKeywords(ids: Set<String>): Flow<DocIdentifier>

	suspend fun modifyKeyword(keyword: Keyword): Keyword?
	fun getKeywordsByUser(userId: String): Flow<Keyword>
}
