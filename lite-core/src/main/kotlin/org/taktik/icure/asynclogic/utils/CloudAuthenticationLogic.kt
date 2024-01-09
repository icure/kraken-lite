package org.taktik.icure.asynclogic.utils

/**
 * Logic for authentication from the current instance of icure-oss to icure-cloud.
 * Implementations of this interface may have additional requirements in order for
 * everything to work properly.
 */
interface CloudAuthenticationLogic {
	/**
	 * Get the value of an authorization header for authentication to the icure cloud as the user with the provided id.
	 * @param userId id of the user you want to authenticate as.
	 * @return the authorization header value for the user.
	 */
	suspend fun userAuthenticationHeader(userId: String): String
}
