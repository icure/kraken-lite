package org.taktik.icure.e2e

import java.util.UUID

fun uuid(): String = UUID.randomUUID().toString()

/**
 * Fake security metadata for the encrypted entities.
 *
 * Ported from `cloud-core/src/test/kotlin/org/taktik/icure/test/Delegations.kt`, minus the group handling:
 * kraken-cloud prefixes data owner ids with `"$groupId/"`, but kraken-lite is groupless
 * (`LocalDatastoreInformation.getFullIdFor(id) = id`), so the bare data owner id is what the server expects.
 *
 * No real cryptography is involved. The delegation only has to be well formed enough for the server side
 * access control and for the entity to be routed to, stored in and read back from the right database - which
 * is exactly what the e2e suite is checking. Real client side encryption is the SDK's job.
 */
fun fakeSecureDelegation(dataOwnerId: String): Pair<String, Map<String, Any?>> =
	uuid() to mapOf(
		"delegator" to dataOwnerId,
		"delegate" to dataOwnerId,
		"secretIds" to emptyList<String>(),
		"encryptionKeys" to emptyList<String>(),
		"owningEntityIds" to emptyList<String>(),
		"parentDelegations" to emptyList<String>(),
		"exchangeDataId" to uuid(),
		"permissions" to "WRITE",
	)

/**
 * Builds the `securityMetadata` block granting write access to [dataOwnerId].
 */
fun securityMetadataFor(dataOwnerId: String): Map<String, Any?> =
	mapOf("secureDelegations" to mapOf(fakeSecureDelegation(dataOwnerId)))

/**
 * Builds the legacy `delegations` block, still honoured alongside the secure delegations.
 */
fun legacyDelegationsFor(dataOwnerId: String): Map<String, Any?> =
	mapOf(dataOwnerId to listOf(mapOf("owner" to dataOwnerId, "delegatedTo" to dataOwnerId)))

/**
 * Adds both the modern and the legacy delegation to an entity payload, so the created entity is readable
 * whichever path the server takes.
 */
fun Map<String, Any?>.withDelegationFor(dataOwnerId: String): Map<String, Any?> =
	this + mapOf(
		"securityMetadata" to securityMetadataFor(dataOwnerId),
		"delegations" to legacyDelegationsFor(dataOwnerId),
	)
