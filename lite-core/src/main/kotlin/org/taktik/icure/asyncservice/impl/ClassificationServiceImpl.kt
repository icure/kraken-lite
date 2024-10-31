package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.ClassificationLogic
import org.taktik.icure.asyncservice.ClassificationService
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.Classification
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult

@Service
class ClassificationServiceImpl(
    private val classificationLogic: ClassificationLogic
) : ClassificationService {
    override suspend fun createClassification(classification: Classification): Classification? = classificationLogic.createClassification(classification)

    override suspend fun getClassification(classificationId: String): Classification? = classificationLogic.getClassification(classificationId)

    @Suppress("DEPRECATION")
    @Deprecated("This method cannot include results with secure delegations, use listClassificationIdsByDataOwnerPatientCreated instead")
    override fun listClassificationsByHCPartyAndSecretPatientKeys(
        hcPartyId: String,
        secretPatientKeys: List<String>
    ): Flow<Classification> = classificationLogic.listClassificationsByHCPartyAndSecretPatientKeys(hcPartyId, secretPatientKeys)

    override fun listClassificationIdsByDataOwnerPatientCreated(
        dataOwnerId: String,
        secretForeignKeys: Set<String>,
        startDate: Long?,
        endDate: Long?,
        descending: Boolean
    ): Flow<String> = classificationLogic.listClassificationIdsByDataOwnerPatientCreated(dataOwnerId, secretForeignKeys, startDate, endDate, descending)

    override fun deleteClassifications(ids: List<IdAndRev>): Flow<DocIdentifier> = classificationLogic.deleteEntities(ids)
    override suspend fun deleteClassification(id: String, rev: String?): DocIdentifier = classificationLogic.deleteEntity(id, rev)
    override suspend fun purgeClassification(id: String, rev: String): DocIdentifier = classificationLogic.purgeEntity(id, rev)
    override suspend fun undeleteClassification(id: String, rev: String): Classification = classificationLogic.undeleteEntity(id, rev)
    override suspend fun modifyClassification(classification: Classification): Classification? = classificationLogic.modifyEntities(setOf(classification)).single()

    override suspend fun addDelegation(
        classificationId: String,
        healthcarePartyId: String,
        delegation: Delegation
    ): Classification? = getClassification(classificationId)?.let {
        classificationLogic.addDelegation(it, healthcarePartyId, delegation)
    }

    override suspend fun addDelegations(classificationId: String, delegations: List<Delegation>): Classification? = getClassification(classificationId)?.let {
        classificationLogic.addDelegations(it, delegations)
    }

    override fun getClassifications(ids: List<String>): Flow<Classification> = classificationLogic.getClassifications(ids)

    override fun modifyEntities(entities: Collection<Classification>): Flow<Classification> = classificationLogic.modifyEntities(entities)
    override fun matchClassificationsBy(filter: AbstractFilter<Classification>): Flow<String> = classificationLogic.matchEntitiesBy(filter)

    override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<Classification>> = classificationLogic.bulkShareOrUpdateMetadata(requests)
}