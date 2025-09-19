package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.singleOrNull
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.ApplicationSettingsLogic
import org.taktik.icure.asyncservice.ApplicationSettingsService
import org.taktik.icure.entities.ApplicationSettings
import org.taktik.icure.entities.requests.BulkShareOrUpdateMetadataParams

@Service
class ApplicationSettingsServiceImpl(
	private val applicationSettingsLogic: ApplicationSettingsLogic
) : ApplicationSettingsService {
	override suspend fun createApplicationSettings(applicationSettings: ApplicationSettings): ApplicationSettings? = applicationSettingsLogic.createApplicationSettings(applicationSettings)

	override suspend fun modifyApplicationSettings(applicationSettings: ApplicationSettings): ApplicationSettings? = applicationSettingsLogic.modifyEntities(listOf(applicationSettings)).singleOrNull()

	override fun getAllApplicationSettings(): Flow<ApplicationSettings> = applicationSettingsLogic.getEntities()

	override fun bulkShareOrUpdateMetadata(requests: BulkShareOrUpdateMetadataParams) = applicationSettingsLogic.bulkShareOrUpdateMetadata(requests)
}