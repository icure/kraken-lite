package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.DataOwnerLogic
import org.taktik.icure.asyncservice.DataOwnerService
import org.taktik.icure.entities.CryptoActorStubWithType
import org.taktik.icure.entities.DataOwnerType
import org.taktik.icure.entities.DataOwnerWithType
import org.taktik.icure.entities.base.DataOwnerHierarchyInfo
import org.taktik.icure.entities.requests.DataOwnerPublicKeys
import org.taktik.icure.entities.requests.LinkedDataOwner
import org.taktik.icure.pagination.MultiKeyPaginationElement

@Service
class DataOwnerServiceImpl(
    private val dataOwnerLogic: DataOwnerLogic
) : DataOwnerService {
    override suspend fun getCryptoActorStub(dataOwnerId: String): CryptoActorStubWithType? = dataOwnerLogic.getCryptoActorStub(dataOwnerId)
    override fun getCryptoActorStubs(dataOwnerIds: List<String>): Flow<CryptoActorStubWithType> = dataOwnerLogic.getCryptoActorStubs(dataOwnerIds)
    override suspend fun getDataOwner(dataOwnerId: String): DataOwnerWithType? = dataOwnerLogic.getDataOwner(dataOwnerId)
    override fun getDataOwners(dataOwnerIds: List<String>): Flow<DataOwnerWithType> = dataOwnerLogic.getDataOwners(dataOwnerIds)
    override suspend fun modifyCryptoActor(modifiedCryptoActor: CryptoActorStubWithType): CryptoActorStubWithType = dataOwnerLogic.modifyCryptoActor(modifiedCryptoActor)
    override fun getCryptoActorHierarchy(dataOwnerId: String): Flow<DataOwnerWithType> = dataOwnerLogic.getCryptoActorHierarchy(dataOwnerId)
    override fun getCryptoActorHierarchyStub(dataOwnerId: String): Flow<CryptoActorStubWithType> = dataOwnerLogic.getCryptoActorHierarchyStub(dataOwnerId)
    override suspend fun getCryptoActorHierarchyInfo(dataOwnerId: String): DataOwnerHierarchyInfo = dataOwnerLogic.getCryptoActorHierarchyInfo(dataOwnerId)

    override fun findDataOwnersLinkedToGroups(
        dataOwnerGroupIds: List<String>,
        dataOwnerType: DataOwnerType,
        startDocumentId: String?,
        limit: Int,
    ): Flow<MultiKeyPaginationElement<LinkedDataOwner, String>> =
        dataOwnerLogic.findDataOwnersLinkedToGroups(dataOwnerGroupIds, dataOwnerType, startDocumentId, limit)

    override fun getDataOwnersPublicKeys(
        dataOwnerIds: List<String>,
        dataOwnerType: DataOwnerType,
    ): Flow<DataOwnerPublicKeys> = dataOwnerLogic.getDataOwnersPublicKeys(dataOwnerIds, dataOwnerType)

    override fun addDataOwnersToGroup(
        dataOwnerType: DataOwnerType,
        dataOwnerGroupId: String,
        newMembersIds: List<String>,
    ): Flow<String> = dataOwnerLogic.addDataOwnersToGroup(dataOwnerType, dataOwnerGroupId, newMembersIds)

    override fun removeDataOwnersFromGroup(
        dataOwnerType: DataOwnerType,
        dataOwnerGroupId: String,
        membersToRemoveIds: List<String>,
        invalidateSharedExchangeDataIfNeeded: Boolean,
    ): Flow<String> = dataOwnerLogic.removeDataOwnersFromGroup(dataOwnerType, dataOwnerGroupId, membersToRemoveIds, invalidateSharedExchangeDataIfNeeded)
}
