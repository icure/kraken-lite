/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.taktik.icure.security.LiteAuthenticationManager
import org.taktik.icure.security.SecurityToken
import org.taktik.icure.security.UnauthorizedEntryPoint
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import org.taktik.icure.spring.asynccache.Cache

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfigAdapter(
    private val authenticationManager: LiteAuthenticationManager,
    asyncCacheManager: AsyncCacheManager
) : AbstractSecurityConfigAdapter() {

    @Value("\${spring.session.enabled}")
    override val sessionEnabled: Boolean = false

    override val cache: Cache<String, SecurityToken> = asyncCacheManager.getCache("spring.security.tokens")

    val log: Logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity, asyncCacheManager: AsyncCacheManager): SecurityWebFilterChain {
        return http.authorizeExchange { exchange ->
            exchange.pathMatchers("/**").permitAll()
        }.csrf {
            it.disable()
        }.httpBasic(Customizer.withDefaults())
        .addFilterAfter(
            AuthenticationWebFilter(authenticationManager).apply {
                this.setAuthenticationFailureHandler(ServerAuthenticationEntryPointFailureHandler(UnauthorizedEntryPoint()))
                if (sessionEnabled) {
                    this.setSecurityContextRepository(sessionLessSecurityContextRepository)
                } else {
                    this.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                }
                this.setServerAuthenticationConverter(multiTokenAuthConverter)
            },
            SecurityWebFiltersOrder.REACTOR_CONTEXT
        ).build()
    }
}
