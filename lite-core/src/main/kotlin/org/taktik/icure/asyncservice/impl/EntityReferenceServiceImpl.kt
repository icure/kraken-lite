package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.EntityReferenceLogic
import org.taktik.icure.asyncservice.EntityReferenceService
import org.taktik.icure.entities.EntityReference

@Service
class EntityReferenceServiceImpl(
    private val entityReferenceLogic: EntityReferenceLogic
) : EntityReferenceService {
    override suspend fun getLatest(prefix: String): EntityReference? = entityReferenceLogic.getLatest(prefix)

    override fun createEntityReferences(entities: Collection<EntityReference>): Flow<EntityReference> = entityReferenceLogic.createEntities(entities)
}