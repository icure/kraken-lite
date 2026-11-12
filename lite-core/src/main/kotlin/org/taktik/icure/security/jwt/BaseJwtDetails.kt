package org.taktik.icure.security.jwt

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.taktik.icure.entities.DataOwnerType
import org.taktik.icure.security.AbstractUserDetails
import org.taktik.icure.utils.DynamicBitArray
import java.time.Instant

data class BaseJwtDetails(
    override val userId: String,
    override val dataOwnerId: String?,
    override val dataOwnerType: DataOwnerType?,
    override val parentLinkedDataOwnerIds: Set<String>,
    override val simpleLinkedDataOwnerIds: Set<String>,
    override val authorities: Set<GrantedAuthority>,
    override val expiration: Long? = null
) : AbstractUserDetails(), JwtDetails {
    override val principalPermissions: DynamicBitArray = DynamicBitArray.bitVectorOfSize(0)

    companion object : JwtConverter<BaseJwtDetails> {

        override fun fromClaims(claims: Map<String, Any?>): BaseJwtDetails =
            BaseJwtDetails(
                userId = (claims[USER_ID] as String),
                dataOwnerId = claims[DATA_OWNER_ID] as String?,
                dataOwnerType = (claims[DATA_OWNER_TYPE] as String?)?.let { DataOwnerType.valueOfOrNullCaseInsensitive(it) },
                parentLinkedDataOwnerIds = hierarchyEntries(claims).filterNot { it.startsWith(HCP_HIERARCHY_SIMPLE_PREFIX) }.toSet(),
                simpleLinkedDataOwnerIds = hierarchyEntries(claims)
                    .filter { it.startsWith(HCP_HIERARCHY_SIMPLE_PREFIX) }
                    .mapTo(mutableSetOf()) { it.removePrefix(HCP_HIERARCHY_SIMPLE_PREFIX) },
                authorities = (claims[AUTHORITIES] as Collection<Any?>)
                    .filterIsInstance<String>()
                    .fold(setOf<GrantedAuthority>()) { acc, x -> acc + SimpleGrantedAuthority(x) }.toSet(),
                expiration = (claims[Jwt.StandardClaims.EXPIRES_AT] as Instant).epochSecond,
            )

        private fun hierarchyEntries(claims: Map<String, Any?>): List<String> =
            ((claims[HCP_HIERARCHY] ?: emptyList<Any>()) as Collection<Any?>).filterIsInstance<String>()

    }

    override fun toClaimsOmittingExpiration(): Map<String, Any?> = mapOf(
        USER_ID to userId,
        DATA_OWNER_ID to dataOwnerId,
        DATA_OWNER_TYPE to dataOwnerType,
        HCP_HIERARCHY to (parentLinkedDataOwnerIds + simpleLinkedDataOwnerIds.map { "$HCP_HIERARCHY_SIMPLE_PREFIX$it" })
            .takeIf { it.isNotEmpty() }
            ?.toList(),
        AUTHORITIES to authorities.map { it.authority },
    ).filterValues { it != null }

}
