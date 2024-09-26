package org.taktik.icure.asynclogic

interface ICureLiteLogic : ICureLogic {
	suspend fun getCouchDbConfigProperty(section: String, key: String): String?
	suspend fun setCouchDbConfigProperty(section: String, key: String, newValue: String)
}