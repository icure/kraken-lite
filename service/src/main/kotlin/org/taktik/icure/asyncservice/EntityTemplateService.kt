/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncservice

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.entities.EntityTemplate

interface EntityTemplateService {
	suspend fun createEntityTemplate(entityTemplate: EntityTemplate): EntityTemplate?

	suspend fun modifyEntityTemplate(entityTemplate: EntityTemplate): EntityTemplate?

	suspend fun getEntityTemplate(id: String): EntityTemplate?
	fun getEntityTemplates(selectedIds: Collection<String>): Flow<EntityTemplate>

	fun listEntityTemplatesBy(userId: String, entityType: String, searchString: String?, includeEntities: Boolean?): Flow<EntityTemplate>
	fun listEntityTemplatesBy(entityType: String, searchString: String?, includeEntities: Boolean?): Flow<EntityTemplate>
	fun listEntityTemplatesByKeyword(userId: String, entityType: String, keyword: String?, includeEntities: Boolean?): Flow<EntityTemplate>
	fun listEntityTemplatesByKeyword(entityType: String, keyword: String?, includeEntities: Boolean?): Flow<EntityTemplate>

	/**
	 * Modifies [EntityTemplate]s in batch.
	 *
	 * @param entities a [Collection] of modified [EntityTemplate]s.
	 * @return a [Flow] containing all the [EntityTemplate]s that were successfully modified.
	 */
	fun modifyEntityTemplates(entities: Collection<EntityTemplate>): Flow<EntityTemplate>

	/**
	 * Creates [EntityTemplate]s in batch.
	 *
	 * @param entities a [Collection] of [EntityTemplate]s to create.
	 * @return a [Flow] containing all the [EntityTemplate]s that were successfully created.
	 */
	fun createEntityTemplates(entities: Collection<EntityTemplate>): Flow<EntityTemplate>

	/**
	 * Deletes [EntityTemplate]s in batch.
	 * If the user does not meet the precondition to delete [EntityTemplate]s, an error will be thrown.
	 * If the current user does not have the permission to delete one or more elements in
	 * the batch, then those elements will not be deleted and no error will be thrown.
	 *
	 * @param ids a [List] containing the ids of the [EntityTemplate]s to delete.
	 * @return a [Flow] containing the [DocIdentifier]s of the [EntityTemplate]s that were successfully deleted.
	 */
	fun deleteEntityTemplates(ids: Set<String>): Flow<DocIdentifier>
}
