package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.SecureDelegationKeyMapLogic
import org.taktik.icure.asyncservice.SecureDelegationKeyMapService
import org.taktik.icure.entities.SecureDelegationKeyMap
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams
import org.taktik.icure.entities.requests.EntityBulkShareResult

@Service
class SecureDelegationKeyMapServiceImpl(
    private val secureDelegationKeyMapLogic: SecureDelegationKeyMapLogic
) : SecureDelegationKeyMapService {
    override suspend fun createSecureDelegationKeyMap(map: SecureDelegationKeyMap): SecureDelegationKeyMap = secureDelegationKeyMapLogic.createSecureDelegationKeyMap(map)

    override fun findByDelegationKeys(delegationKeys: List<String>): Flow<SecureDelegationKeyMap> = secureDelegationKeyMapLogic.findByDelegationKeys(delegationKeys)

    override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams): Flow<EntityBulkShareResult<SecureDelegationKeyMap>> = secureDelegationKeyMapLogic.bulkShareOrUpdateMetadata(requests)
}
