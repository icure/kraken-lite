package org.taktik.icure.security.jwt

import io.jsonwebtoken.Claims

const val USER_ID = "u"

interface Jwt {

    val userId: String

    fun toClaims(): Map<String, Any?>

}

interface JwtConverter<T : Jwt> {

    fun fromClaims(claims: Claims, jwtExpirationTime: Long): T

}