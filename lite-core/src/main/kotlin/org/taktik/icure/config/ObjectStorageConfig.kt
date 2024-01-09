package org.taktik.icure.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.taktik.icure.asynclogic.objectstorage.DocumentObjectStorageClient
import org.taktik.icure.asynclogic.objectstorage.DocumentObjectStorageClientImpl
import org.taktik.icure.asynclogic.objectstorage.FakeObjectStorageClient
import org.taktik.icure.asynclogic.utils.CloudAuthenticationLogic
import org.taktik.icure.properties.ExternalServicesProperties
import org.taktik.icure.properties.ObjectStorageProperties

@Configuration
class ObjectStorageConfig {
	private val log = LoggerFactory.getLogger(ObjectStorageConfig::class.java)

	@Bean
	fun documentObjectStorageClient(
		objectStorageProperties: ObjectStorageProperties,
		cloudAuthenticationLogic: CloudAuthenticationLogic,
		externalServicesProperties: ExternalServicesProperties,
		@Autowired(required=false) @Qualifier("fakeObjectStorageClientUserCheck") fakeObjectStorageClientUserCheck: ((String) -> Boolean)?
	): DocumentObjectStorageClient =
		if (externalServicesProperties.useFakes) {
			log.warn("Using fake object storage client. This should be done only for testing purposes.")
			FakeObjectStorageClient.document(
				externalServicesProperties,
				null,
				fakeObjectStorageClientUserCheck ?: { true }
			)
		} else {
			DocumentObjectStorageClientImpl(objectStorageProperties, cloudAuthenticationLogic)
		}
}
