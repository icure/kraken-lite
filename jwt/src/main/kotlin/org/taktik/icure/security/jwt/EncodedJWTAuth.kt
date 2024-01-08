package org.taktik.icure.security.jwt

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class EncodedJWTAuth(
    val token: String,
    val claims: JwtDetails? = null,
    private val authorities: MutableSet<GrantedAuthority> = mutableSetOf()
) : Authentication {
    override fun getName(): String = "jwt"
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities
    override fun getCredentials(): Any = Any()
    override fun getDetails(): Any = Any()
    override fun getPrincipal(): Any = Any()
    override fun isAuthenticated(): Boolean = true
    override fun setAuthenticated(isAuthenticated: Boolean) {}
}
