package org.taktik.icure.cache

import org.springframework.stereotype.Component
import org.taktik.icure.entities.CustomEntityBase

@Component
class LiteCustomEntityCache : CustomEntityCache {

	override suspend fun putStub(id: String, entity: CustomEntityBase) { }

	override suspend fun getStub(id: String): CustomEntityBase? = null

	override suspend fun getEntity(id: String): CustomEntityBase? = null

	override suspend fun putInCache(id: String, entity: CustomEntityBase) { }

	override suspend fun evictFromCache(id: String) { }

}