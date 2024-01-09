package org.taktik.icure.asyncservice.impl

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

    override suspend fun getDataOwner(dataOwnerId: String): DataOwnerWithType? = dataOwnerLogic.getDataOwner(dataOwnerId)

    override suspend fun modifyCryptoActor(modifiedCryptoActor: CryptoActorStubWithType): CryptoActorStubWithType = dataOwnerLogic.modifyCryptoActor(modifiedCryptoActor)
}