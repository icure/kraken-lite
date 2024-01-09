package org.taktik.icure.properties

import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("icure.objectstorage")
final data class ObjectStorageProperties(
	/**
	 * Path to the directory to use as local cache and temporary storage for object storage attachments which needs to be uploaded.
	 */
    var cacheLocation: String = "",
	/**
	 * Url to the icure cloud version, for upload to the object storage service.
	 */
    var icureCloudUrl: String = "",
	/**
	 * If true enables automatic migration of existing attachments to object storage if they are above the size limit.
	 */
    var backlogToObjectStorage: Boolean = true,
	/**
	 * Minimum size for new attachments to be stored to object storage (in bytes).
	 */
    var sizeLimit: Long = 134_217_728,
	/**
	 * Minimum size for existing attachments to be migrated to object storage (in bytes).
	 * Must be greater than or equal to [sizeLimit].
	 */
	var migrationSizeLimit: Long = 134_217_728,
	/**
	 * Delay in milliseconds between when a migratable attachment is found and when the migration is actually executed.
	 */
	var migrationDelayMs: Long = 15 * 60 * 1000,
) : InitializingBean {
	override fun afterPropertiesSet() {
		require(migrationSizeLimit >= sizeLimit) {
			"Migration size limit must be greater than or equal to sizeLimit"
		}
	}
}
