package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.ExchangeDataMapLogic
import org.taktik.icure.asyncservice.ExchangeDataMapService
import org.taktik.icure.entities.ExchangeDataMap
import org.taktik.icure.entities.utils.Base64String
import org.taktik.icure.entities.utils.HexString
import org.taktik.icure.entities.utils.KeypairFingerprintV2String

@Service
class ExchangeDataMapServiceImpl(
    private val exchangeDataMapLogic: ExchangeDataMapLogic
) : ExchangeDataMapService {
    override suspend fun modifyExchangeDataMap(
        accessControlKey: String,
        exchangeDataMap: ExchangeDataMap
    ): ExchangeDataMap? = exchangeDataMapLogic.modifyExchangeDataMap(exchangeDataMap)

    override fun createOrUpdateExchangeDataMapBatchByAccessControlKey(batch: Map<HexString, Map<KeypairFingerprintV2String, Base64String>>): Flow<ExchangeDataMap> =
        exchangeDataMapLogic.createOrUpdateExchangeDataMapBatchByAccessControlKey(batch)

    override fun getExchangeDataMapBatch(ids: Collection<String>): Flow<ExchangeDataMap> =
        exchangeDataMapLogic.getExchangeDataMapBatch(ids)
}