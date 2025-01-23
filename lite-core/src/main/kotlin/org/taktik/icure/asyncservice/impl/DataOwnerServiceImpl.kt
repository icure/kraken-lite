package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.DataOwnerLogic
import org.taktik.icure.asyncservice.DataOwnerService
import org.taktik.icure.entities.CryptoActorStubWithType
import org.taktik.icure.entities.DataOwnerWithType

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
}