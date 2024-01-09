package org.taktik.icure.asynclogic.objectstorage.testutils

import org.taktik.icure.properties.ObjectStorageProperties

fun testObjectStorageProperties(
	sizeLimit: Long = 2_000,
	migrationSizeLimit: Long = 4_000,
	migrationDelayMs: Long = 15 * 60 * 1000
) = ObjectStorageProperties(
	icureCloudUrl = "test",
	cacheLocation = testLocalStorageDirectory,
	backlogToObjectStorage = true,
	sizeLimit = sizeLimit,
	migrationSizeLimit = migrationSizeLimit,
	migrationDelayMs = migrationDelayMs
)
