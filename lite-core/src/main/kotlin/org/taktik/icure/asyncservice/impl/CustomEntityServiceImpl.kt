package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.CustomEntityLogic
import org.taktik.icure.asynclogic.objectstorage.DataAttachmentChange
import org.taktik.icure.asyncservice.CustomEntityService
import org.taktik.icure.entities.CustomEntityBase
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult
import org.taktik.icure.exceptions.NotFoundRequestException

@Service
class CustomEntityServiceImpl(
	private val customEntityLogic: CustomEntityLogic
) : CustomEntityService {

	override suspend fun createCustomEntity(
		entityType: String,
		entity: CustomEntityBase
	): CustomEntityBase = customEntityLogic.createCustomEntity(entityType, entity)

	override fun createCustomEntities(
		entityType: String,
		entities: List<CustomEntityBase>
	): Flow<CustomEntityBase> = customEntityLogic.createCustomEntities(entityType, entities)

	override suspend fun getCustomEntity(
		entityType: String,
		id: String
	): CustomEntityBase? = customEntityLogic.getCustomEntity(entityType, id)

	override fun getCustomEntities(
		entityType: String,
		ids: List<String>
	): Flow<CustomEntityBase> = customEntityLogic.getCustomEntities(entityType, ids)

	override fun getCustomEntitiesMetadataStubs(
		entityType: String,
		ids: List<String>
	): Flow<CustomEntityBase> = customEntityLogic.getCustomEntitiesMetadataStubLenient(entityType, ids)

	override suspend fun modifyCustomEntity(
		entityType: String,
		entity: CustomEntityBase
	): CustomEntityBase = customEntityLogic.modifyCustomEntity(entityType, entity)

	override fun modifyCustomEntities(
		entityType: String,
		entities: List<CustomEntityBase>
	): Flow<CustomEntityBase> = customEntityLogic.modifyCustomEntities(entityType, entities)

	override suspend fun deleteCustomEntity(
		entityType: String,
		id: String,
		rev: String?
	): CustomEntityBase = customEntityLogic.deleteCustomEntity(entityType, id, rev)

	override fun deleteCustomEntities(
		entityType: String,
		identifiers: Collection<IdAndRev>
	): Flow<CustomEntityBase> = customEntityLogic.deleteCustomEntities(entityType, identifiers)

	override suspend fun undeleteCustomEntity(
		entityType: String,
		id: String,
		rev: String?
	): CustomEntityBase = customEntityLogic.undeleteCustomEntity(entityType, id, rev)

	override fun undeleteCustomEntities(
		entityType: String,
		identifiers: Collection<IdAndRev>
	): Flow<CustomEntityBase> = customEntityLogic.undeleteCustomEntities(entityType, identifiers)

	override suspend fun purgeCustomEntity(
		entityType: String,
		id: String,
		rev: String
	): DocIdentifier = customEntityLogic.purgeCustomEntity(entityType, id, rev)

	override fun purgeCustomEntities(
		entityType: String,
		identifiers: Collection<IdAndRev>
	): Flow<DocIdentifier> = customEntityLogic.purgeCustomEntities(entityType, identifiers)

	override fun bulkShareOrUpdateMetadata(
		entityType: String,
		requests: BulkShareOrUpdateMetadataParams
	): Flow<EntityBulkShareResult<CustomEntityBase>> = customEntityLogic.bulkShareOrUpdateMetadata(entityType, requests)

	override suspend fun getAttachment(
		entityType: String,
		entityId: String,
		attachmentKey: String
	): Pair<CustomEntityBase, Flow<DataBuffer>> {
		// A metadata stub is enough to load the attachment, and also checks that the entity exists and has the expected type
		val entity = customEntityLogic.getCustomEntityMetadataStub(entityType, entityId)
			?: throw NotFoundRequestException("Entity $entityId does not exist")
		return entity to customEntityLogic.getAttachment(entityType, entity, attachmentKey)
	}

	override suspend fun updateAttachments(
		entityType: String,
		entityId: String,
		entityRev: String,
		changes: Map<String, DataAttachmentChange>
	): CustomEntityBase? = customEntityLogic.updateAttachments(entityType, entityId, entityRev, changes)

}