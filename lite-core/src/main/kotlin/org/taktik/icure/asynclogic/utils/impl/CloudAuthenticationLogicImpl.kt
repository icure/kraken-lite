package org.taktik.icure.asynclogic.utils.impl

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.utils.CloudAuthenticationLogic
import org.taktik.icure.asynclogic.utils.InstallationConstantLogic
import java.util.*
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap

/**
 * Implementation of [CloudAuthenticationLogic] which uses iCure authentication tokens.
 * This implementation only works if the synchronisation between the local couchdb instance
 * and the cloud couchdb instance is properly set up.
 */
@Service
class CloudAuthenticationLogicImpl(
	private val installationConstantLogic: InstallationConstantLogic,
	private val userLogic: UserLogic,
) : CloudAuthenticationLogic {
	companion object {
		private const val TOKEN_EXPIRATION_MARGIN_MS = 5 * 60 * 1000L
		private const val TOKEN_VALIDITY_SECONDS = 60 * 60L
	}

	private val userTokenCache = ConcurrentHashMap<String, Deferred<CachedToken>>()

	override suspend fun userAuthenticationHeader(userId: String): String =
		"Basic ${Base64.getEncoder().encodeToString(("$userId:${getUserToken(userId)}").toByteArray())}"

	// As long as there is only one instance per installation running the token should be valid until expiration
	private suspend fun getUserToken(userId: String) =
		userTokenCache[userId]
			?.runCatching { await() }
			?.getOrNull()
			?.takeIf { it.expirationEpochMs > (System.currentTimeMillis() + TOKEN_EXPIRATION_MARGIN_MS) }
			?.value
			?: cacheAndGetNewUserToken(userId)

	private suspend fun cacheAndGetNewUserToken(userId: String): String = coroutineScope {
		val myJob: Deferred<CachedToken> = async(start = CoroutineStart.LAZY) { getNewUserToken(userId) }
		var lastTriedJob: Deferred<CachedToken>
		var lastJobResult: Result<CachedToken>
		do {
			lastTriedJob = userTokenCache.putIfAbsent(userId, myJob) ?: myJob
			lastJobResult = kotlin.runCatching { lastTriedJob.await() }
			ensureActive()
			// If the job failed because another coroutine started it and then was cancelled I should try again.
		} while (lastTriedJob !== myJob && lastJobResult.exceptionOrNull() is CancellationException)
		lastJobResult.getOrThrow().value
	}

	private suspend fun getNewUserToken(userId: String): CachedToken {
		val now = System.currentTimeMillis()
		return CachedToken(
			userLogic.createOrUpdateToken(userId, "cloudConnection_${installationConstantLogic.getInstallationId()}", TOKEN_VALIDITY_SECONDS),
			now + TOKEN_VALIDITY_SECONDS * 1000L
		)
	}

	data class CachedToken(
		val value: String,
		val expirationEpochMs: Long
	)
}
