package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.FrontEndMigrationLogic
import org.taktik.icure.asyncservice.FrontEndMigrationService
import org.taktik.icure.entities.FrontEndMigration

@Service
class FrontEndMigrationServiceImpl(
    private val frontEndMigrationLogic: FrontEndMigrationLogic
) : FrontEndMigrationService {
    override suspend fun createFrontEndMigration(frontEndMigration: FrontEndMigration): FrontEndMigration? = frontEndMigrationLogic.createFrontEndMigration(frontEndMigration)
    override suspend fun deleteFrontEndMigration(frontEndMigrationId: String): FrontEndMigration? = frontEndMigrationLogic.deleteEntity(frontEndMigrationId, null)

    override suspend fun getFrontEndMigration(frontEndMigrationId: String): FrontEndMigration? = frontEndMigrationLogic.getFrontEndMigration(frontEndMigrationId)

    override fun getFrontEndMigrationByUserIdName(userId: String, name: String?): Flow<FrontEndMigration> = frontEndMigrationLogic.getFrontEndMigrationByUserIdName(userId, name)

    override suspend fun modifyFrontEndMigration(frontEndMigration: FrontEndMigration): FrontEndMigration? = frontEndMigrationLogic.modifyFrontEndMigration(frontEndMigration)
}