package org.taktik.icure.security.jwt

import io.jsonwebtoken.Claims
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.taktik.icure.constants.Roles
import org.taktik.icure.entities.DataOwnerType
import org.taktik.icure.security.AbstractUserDetails
import org.taktik.icure.utils.DynamicBitArray

data class BaseJwtDetails(
    override val userId: String,
    override val dataOwnerId: String?,
    override val dataOwnerType: DataOwnerType?,
    override val hcpHierarchy: List<String>,
    override val authorities: Set<GrantedAuthority>
) : AbstractUserDetails(), JwtDetails {
    override val principalPermissions: DynamicBitArray = DynamicBitArray.bitVectorOfSize(0)

    companion object : JwtConverter<BaseJwtDetails> {

        override fun fromClaims(claims: Claims, jwtExpirationTime: Long): BaseJwtDetails =
            BaseJwtDetails(
                userId = (claims[USER_ID] as String),
                dataOwnerId = claims[DATA_OWNER_ID] as String?,
                dataOwnerType = (claims[DATA_OWNER_TYPE] as String?)?.let { DataOwnerType.valueOfOrNullCaseInsensitive(it) },
                hcpHierarchy = ((claims[HCP_HIERARCHY] ?: emptyList<Any>()) as Collection<Any?>).mapNotNull { it as? String },
                authorities = (claims[AUTHORITIES] as Collection<Any?>)
                    .filterIsInstance<String>()
                    .fold(setOf<GrantedAuthority>()) { acc, x -> acc + SimpleGrantedAuthority(x) }.toSet(),
            )

    }

    override fun toClaims(): Map<String, Any?> = mapOf(
        USER_ID to userId,
        DATA_OWNER_ID to dataOwnerId,
        DATA_OWNER_TYPE to dataOwnerType,
        HCP_HIERARCHY to hcpHierarchy,
        AUTHORITIES to authorities.map { it.authority }
    ).filterValues { it != null }

}
