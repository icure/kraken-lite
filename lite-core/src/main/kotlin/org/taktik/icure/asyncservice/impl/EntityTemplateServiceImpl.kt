package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.EntityTemplateLogic
import org.taktik.icure.asyncservice.EntityTemplateService
import org.taktik.icure.entities.EntityTemplate

@Service
class EntityTemplateServiceImpl(
    private val entityTemplateLogic: EntityTemplateLogic
) : EntityTemplateService {
    override suspend fun createEntityTemplate(entityTemplate: EntityTemplate): EntityTemplate? = entityTemplateLogic.createEntityTemplate(entityTemplate)

    override suspend fun modifyEntityTemplate(entityTemplate: EntityTemplate): EntityTemplate? = entityTemplateLogic.modifyEntityTemplate(entityTemplate)

    override suspend fun getEntityTemplate(id: String): EntityTemplate? = entityTemplateLogic.getEntityTemplate(id)

    override fun getEntityTemplates(selectedIds: Collection<String>): Flow<EntityTemplate> = entityTemplateLogic.getEntityTemplates(selectedIds)

    override fun listEntityTemplatesBy(
        userId: String,
        entityType: String,
        searchString: String?,
        includeEntities: Boolean?
    ): Flow<EntityTemplate> = entityTemplateLogic.listEntityTemplatesBy(userId, entityType, searchString, includeEntities)

    override fun listEntityTemplatesBy(
        entityType: String,
        searchString: String?,
        includeEntities: Boolean?
    ): Flow<EntityTemplate> = entityTemplateLogic.listEntityTemplatesBy(entityType, searchString, includeEntities)

    override fun listEntityTemplatesByKeyword(
        userId: String,
        entityType: String,
        keyword: String?,
        includeEntities: Boolean?
    ): Flow<EntityTemplate> = entityTemplateLogic.listEntityTemplatesByKeyword(userId, entityType, keyword, includeEntities)

    override fun listEntityTemplatesByKeyword(
        entityType: String,
        keyword: String?,
        includeEntities: Boolean?
    ): Flow<EntityTemplate> = entityTemplateLogic.listEntityTemplatesByKeyword(entityType, keyword, includeEntities)

    override fun modifyEntityTemplates(entities: Collection<EntityTemplate>): Flow<EntityTemplate> = entityTemplateLogic.modifyEntities(entities)

    override fun createEntityTemplates(entities: Collection<EntityTemplate>): Flow<EntityTemplate> = entityTemplateLogic.createEntities(entities)

    override fun deleteEntityTemplates(ids: Set<String>): Flow<DocIdentifier> = entityTemplateLogic.deleteEntities(ids)
}