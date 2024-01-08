package org.taktik.icure.services.external.rest.v2.dto

import org.taktik.icure.entities.utils.Base64String
import org.taktik.icure.entities.utils.HexString
import org.taktik.icure.entities.utils.KeypairFingerprintV2String

data class ExchangeDataMapCreationBatch(
    /**
     * Each entry of this map can be used to create a new ExchangeDataMap. Each key is the hex-encoded access control
     * key while the value is another map that associated the encrypted ExchangeData id to the fingerprint
     * of the public key used to encrypt it.
     */
    val batch: Map<HexString, Map<KeypairFingerprintV2String, Base64String>> = emptyMap()

)