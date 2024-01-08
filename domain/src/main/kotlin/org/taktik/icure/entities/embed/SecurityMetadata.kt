package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.ExchangeData
import org.taktik.icure.entities.utils.Sha256HexString
import org.taktik.icure.exceptions.MergeConflictException
import org.taktik.icure.security.DataOwnerAuthenticationDetails
import org.taktik.icure.utils.DirectedGraphMap
import org.taktik.icure.utils.hasLoops
import java.io.Serializable

/**
 * Holds information for user-based access control and encryption of entities.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class SecurityMetadata(
    /**
     * This maps the hex-encoded sha256 hash of a key created by the client using a certain [ExchangeData.accessControlSecret] to the
     * [SecureDelegation] for the corresponding delegate-delegator pair. This hash is used by the server to perform access control for
     * anonymous data owners (see [DataOwnerAuthenticationDetails]) and in some cases also by the sdks to quickly find the appropriate
     * exchange key needed for the decryption of the content of the corresponding [SecureDelegation].
     *
     * Note that it is also possible for a secure delegation in this map to have no entry for secretId, encryptionKey or owningEntityId.
     * This could happen in situations where a user should have access only to the unencrypted content of an entity.
     */
    val secureDelegations: Map<Sha256HexString, SecureDelegation>,
    /**
     * Holds aliases for secure delegation keys that apply to this entity: `a -> b` means that anyone with key `a` has access to the
     * secure delegation in `secureDelegations['b']`.
     *
     * This map is useful in cases when it is not possible to know for certain if the delegate of a new secure delegation will be able
     * to produce the access control key we are planning to use. For example the access control key may be produced by a combination of
     * access control secret and secret foreign key of the entity: what happens if the entity has multiple secret foreign keys? The
     * delegate may have access to only one of them but not all, so if we chose an unlucky secret foreign key in the creation of the
     * access control key the delegate will never be able to access the entity. This field allows to essentially create an access control
     * key and corresponding secure delegation key for each secret foreign key without having to replicate the actual secure delegation.
     */
    val keysEquivalences: Map<Sha256HexString, Sha256HexString> = emptyMap()
): Serializable {
    init {
        require(secureDelegations.isNotEmpty()) { "Security metadata should contain at least an entry for delegations" }
        require(!secureDelegations.parentsGraph.hasLoops()) { "Secure delegations graph must not have any loops" }
    }

    /**
     * Get a secure delegation using either the canonical hash or an alias.
     * @param hashOrAlias a canonical hash for a delegation or an alias
     * @return the canonical hash and delegation corresponding to the provided hash or alias if it exists.
     */
    fun getDelegation(hashOrAlias: String): Pair<String, SecureDelegation>? =
        this.secureDelegations[hashOrAlias]?.let { hashOrAlias to it }
            ?: this.keysEquivalences[hashOrAlias]?.let { it to this.secureDelegations.getValue(it) }

    /**
     * Get all aliases for a specific hash in this metadata (aliases are metadata-specific).
     * @param hash an access control hash.
     * @throws IllegalArgumentException if [hash] is not in this metadata
     * @return a set containing [hash] and all is aliases.
     */
    fun allAliasesOf(hash: String): Set<String> {
        val aliasedTo = requireNotNull(hash.takeIf { it in secureDelegations } ?: keysEquivalences[hash]) {
            "Provided hash is not part of this metadata"
        }
        return keysEquivalences.asSequence().filter { it.value == aliasedTo }.map { it.value }.toSet() + aliasedTo
    }


    /**
     * Merges the security metadata of two versions of the same entity (same id).
     * NOTE: not suitable for the merging of metadata of duplicate entities (e.g. same person existing as two different
     * patient entities): use [mergeForDuplicatedEntityIntoThisFrom] instead.
     * This method is merges equivalent secure delegations into a single one by doing the union of their content.
     * @param other the security metadata of the other version of the entity.
     * @return the merged security metadata.
     */
    fun mergeForDifferentVersionsOfEntity(other: SecurityMetadata): SecurityMetadata = merge(other, true)

    /**
     * Merges the security metadata of duplicated entities (e.g. different patient entities representing the same
     * person).
     * NOTE: not suitable for the merging of metadata of different versions of the same entity (same id, different
     * revision history): use [mergeForDifferentVersionsOfEntity] instead.
     * The main differences with [mergeForDifferentVersionsOfEntity] are:
     * - This method is not commutative
     * - The encrypted encryption keys from delegations of [other] will not be merged into this.
     * - The parents of equivalent delegations are merged as follows:
     *   - If a delegation is root in this and/or other it will be a root delegation in the merged metadata as
     *     well (potentially removing links which exist in one of the delegations).
     *   - If a delegation is not a root in neither this/other the new parents will be the union of the parents of the
     *   two delegations (potentially updating the parents to a new canonical delegation key).
     * @param other the security metadata of the duplicated entity.
     * @return a new security metadata being the result of the merging.
     */
    fun mergeForDuplicatedEntityIntoThisFrom(other: SecurityMetadata): SecurityMetadata = merge(other, false)

    private fun merge(
        other: SecurityMetadata,
        mergeVersions: Boolean
    ): SecurityMetadata {
        // 1. Align canonical key of secure delegations for this and other.
        // full equivalences: include also x->x (in addition to y->x)
        val thisFullEquivalences = this.keysEquivalences + this.secureDelegations.keys.associateWith { it }
        val otherFullEquivalences = other.keysEquivalences + other.secureDelegations.keys.associateWith { it }
        val mergedFullEquivalences = thisFullEquivalences + otherFullEquivalences.mapValues { (delegationKey, canonicalKey) ->
            thisFullEquivalences[delegationKey] ?: canonicalKey
        }
        // 2. Find duplicate delegations and merge
        val mergedDelegations = mergedFullEquivalences.values.distinct().associateWith { canonicalKey ->
            val thisDelegation = thisFullEquivalences[canonicalKey]?.let { this.secureDelegations.getValue(it) }
            val otherDelegation = otherFullEquivalences[canonicalKey]?.let { other.secureDelegations.getValue(it) }
            if (thisDelegation != null && otherDelegation != null)
                mergeSecDels(thisDelegation, otherDelegation, mergeVersions)
            else
                checkNotNull(thisDelegation ?: otherDelegation) {
                    "At least one of the delegations should have been not null"
                }
        }.mapValues { (_, mergedDelegation) ->
            // 3. Use updated canonical keys for parents
            mergedDelegation.copy(
                parentDelegations = mergedDelegation.parentDelegations.map { originalParent ->
                    mergedFullEquivalences.getValue(originalParent)
                }.toSet()
            )
        }
        return SecurityMetadata(
            secureDelegations = mergedDelegations,
            keysEquivalences = mergedFullEquivalences.filter { (k, v) -> k != v }
        )
    }

    private fun mergeSecDels(
        thisDelegation: SecureDelegation,
        otherDelegation: SecureDelegation,
        mergeVersions: Boolean
    ): SecureDelegation {
        if (
            thisDelegation.delegator != otherDelegation.delegator
            || thisDelegation.delegate != otherDelegation.delegate
            || thisDelegation.exchangeDataId != otherDelegation.exchangeDataId
        ) throw MergeConflictException(
            "Can't merge secure delegations referring to different delegator, delegate or exchange data id"
        )
        return SecureDelegation(
            delegator = thisDelegation.delegator,
            delegate = thisDelegation.delegate,
            secretIds = thisDelegation.secretIds + otherDelegation.secretIds,
            encryptionKeys = if (mergeVersions) {
                thisDelegation.encryptionKeys + otherDelegation.encryptionKeys
            } else {
                thisDelegation.encryptionKeys
            },
            owningEntityIds = thisDelegation.owningEntityIds + otherDelegation.owningEntityIds,
            parentDelegations = if (mergeVersions) {
                thisDelegation.parentDelegations + otherDelegation.parentDelegations
            } else {
                if (thisDelegation.parentDelegations.isEmpty() || otherDelegation.parentDelegations.isEmpty())
                    emptySet()
                else
                    thisDelegation.parentDelegations + otherDelegation.parentDelegations
            },
            exchangeDataId = thisDelegation.exchangeDataId,
            // Without fine-grained permissions this is fine
            permissions = if (thisDelegation.permissions == AccessLevel.WRITE) AccessLevel.WRITE else otherDelegation.permissions
        )
    }
}

val Map<Sha256HexString, SecureDelegation>.parentsGraph: DirectedGraphMap<String> get() =
    this.mapValues { (_, delegation) -> delegation.parentDelegations }
