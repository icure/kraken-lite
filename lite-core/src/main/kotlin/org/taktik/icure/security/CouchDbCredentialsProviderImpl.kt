package org.taktik.icure.security

import org.springframework.stereotype.Service
import org.taktik.icure.properties.CouchDbPropertiesImpl

@Service
class CouchDbCredentialsProviderImpl(
    private val couchDbPropertiesImpl: CouchDbPropertiesImpl
) : CouchDbCredentialsProvider {

    private val userCredentials: UsernamePassword = UsernamePassword(couchDbPropertiesImpl.username!!, couchDbPropertiesImpl.password!!)
    override fun getCredentials(): UsernamePassword {
        return userCredentials
    }
}