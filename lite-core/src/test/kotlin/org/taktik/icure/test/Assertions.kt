package org.taktik.icure.test

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotContainExactly
import kotlinx.coroutines.delay

infix fun ByteArray?.shouldContainExactly(other: ByteArray) =
	this?.toList() shouldContainExactly other.toList()

infix fun ByteArray?.shouldNotContainExactly(other: ByteArray) =
	this?.toList() shouldNotContainExactly other.toList()

suspend fun retryUntil(maxRetry: Int, delayMs: Long, block: suspend() -> Boolean): Boolean =
	(1..maxRetry).fold(false) { success, i ->
		println("Retrying $i")
		if (!success) {
			(runCatching { block() }.onFailure { it.printStackTrace() }.getOrNull() == true)
				.also {if (!it) delay(delayMs) }
		} else true
	}
