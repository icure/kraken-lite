package org.taktik.icure.asyncservice.impl

import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.RecoveryDataLogic
import org.taktik.icure.asyncservice.RecoveryDataService
import org.taktik.icure.entities.RecoveryData

@Service
class RecoveryDataServiceImpl(
    private val recoveryDataLogic: RecoveryDataLogic
) : RecoveryDataService {
    override suspend fun createRecoveryData(recoveryData: RecoveryData): RecoveryData = recoveryDataLogic.createRecoveryData(recoveryData)

    override suspend fun getRecoveryData(id: String): RecoveryData? = recoveryDataLogic.getRecoveryData(id)

    override suspend fun purgeRecoveryData(id: String): DocIdentifier = recoveryDataLogic.purgeRecoveryData(id)

    override suspend fun deleteAllRecoveryDataForRecipient(recipientId: String): Int = recoveryDataLogic.deleteAllRecoveryDataForRecipient(recipientId)

    override suspend fun deleteAllRecoveryDataOfTypeForRecipient(type: RecoveryData.Type, recipientId: String): Int = recoveryDataLogic.deleteAllRecoveryDataOfTypeForRecipient(type, recipientId)
}
