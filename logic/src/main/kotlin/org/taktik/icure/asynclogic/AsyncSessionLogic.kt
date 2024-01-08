package org.taktik.icure.asynclogic

import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.Authentication
import org.springframework.web.server.WebSession
import javax.servlet.http.HttpSession

interface AsyncSessionLogic {
    suspend fun login(username: String, password: String, request: ServerHttpRequest, session: WebSession? = null, groupId: String?): Authentication?

    suspend fun logout()

    fun getOrCreateSession(): HttpSession?

    suspend fun getAuthentication(): Authentication
}