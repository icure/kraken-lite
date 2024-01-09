package org.taktik.icure.asynclogic.utils

/**
 * Provides access to installation constants.
 * Installation constants are values which may be randomly generated on first use, but will stay constant, even after an application restart.
 */
interface InstallationConstantLogic {
	/**
	 * Unique id of the installation.
	 */
	suspend fun getInstallationId(): String
}
