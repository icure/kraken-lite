package org.taktik.icure.security.jwt

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

data class JwtAuthenticationToken<out T : JwtDetails>(
    private val authorities: MutableSet<GrantedAuthority> = mutableSetOf(),
    private val encodedJwt: String,
    private val claims: T,
    private val details: Map<String, Any> = mapOf(),
    private var authenticated: Boolean = false
): Authentication {
    override fun getName(): String = "jwt"
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities
    override fun getCredentials(): Any = encodedJwt
    override fun getDetails(): Any = details
    override fun getPrincipal(): Any = claims
    override fun isAuthenticated(): Boolean = authenticated
    override fun setAuthenticated(isAuthenticated: Boolean) {
        authenticated = isAuthenticated
    }
}