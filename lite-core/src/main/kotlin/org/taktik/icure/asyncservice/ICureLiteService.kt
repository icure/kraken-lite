package org.taktik.icure.asyncservice

interface ICureLiteService {
	suspend fun getCouchDbConfigProperty(section: String, key: String): String?
	suspend fun setCouchDbConfigProperty(section: String, key: String, newValue: String)
}