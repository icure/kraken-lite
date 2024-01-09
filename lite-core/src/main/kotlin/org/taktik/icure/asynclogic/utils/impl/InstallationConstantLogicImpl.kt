package org.taktik.icure.asynclogic.utils.impl

import org.springframework.stereotype.Service
import org.taktik.couchdb.exception.CouchDbException
import org.taktik.icure.asyncdao.utils.InstallationConstantDAO
import org.taktik.icure.asynclogic.utils.InstallationConstantLogic
import org.taktik.icure.entities.utils.InstallationConstant
import java.util.*

@Service
class InstallationConstantLogicImpl(
	private val installationConstantDAO: InstallationConstantDAO
) : InstallationConstantLogic {
	companion object {
		private val INSTALLATION_ID = "icure.installationConstant.installationId"
	}

	private lateinit var installationId: String
	override suspend fun getInstallationId(): String =
		if (this::installationId.isInitialized) {
			installationId
		} else {
			getOrCreate(
				INSTALLATION_ID,
				valueProvider = { UUID.randomUUID().toString() },
				valueParser = { checkNotNull(it) { "Installation id can't be null" } }
			).also { installationId = it }
		}

	private suspend fun <T> getOrCreate(
		installationConstantId: String,
		valueProvider: () -> String,
		valueParser: (String?) -> T
	): T = valueParser(
		(
			installationConstantDAO.get(installationConstantId) ?: try {
				checkNotNull(installationConstantDAO.save(InstallationConstant(installationConstantId, value = valueProvider()))) {
					"Save installation constant did not fail but return null"
				}
			} catch (_: CouchDbException) {
				checkNotNull(installationConstantDAO.get(installationConstantId)) {
					"Installation constant could not be saved but there is no existing installation constant for $installationConstantId"
				}
			}
		).value
	)
}
