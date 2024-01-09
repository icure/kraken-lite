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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.security.web.firewall.StrictHttpFirewall
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler
import org.springframework.security.web.server.authentication.ServerHttpBasicAuthenticationConverter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.AndServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.web.server.ServerWebExchange
import org.taktik.icure.security.LiteAuthenticationManager
import org.taktik.icure.security.TokenWebExchangeMatcher
import org.taktik.icure.security.UnauthorizedEntryPoint
import org.taktik.icure.security.database.ShaAndVerificationCodePasswordEncoder
import org.taktik.icure.security.jwt.EncodedJwtAuthenticationToken
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import reactor.core.publisher.Mono

@ExperimentalCoroutinesApi
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfigAdapter(
    private val authenticationManager: LiteAuthenticationManager
) {

    @Value("\${spring.session.enabled}")
    private val sessionEnabled: Boolean = false

    val log: Logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity, asyncCacheManager: AsyncCacheManager): SecurityWebFilterChain {
        return http
            .authorizeExchange()
            .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .pathMatchers("/v3/api-docs/v*").permitAll()
            .pathMatchers("/api/**").permitAll()
            .pathMatchers("/rest/*/replication/group/**").hasAnyRole("USER", "BOOTSTRAP")
            .pathMatchers("/rest/*/auth/login").permitAll()
            .pathMatchers("/rest/*/auth/refresh").permitAll()
            .pathMatchers("/rest/*/auth/invalidate").permitAll()
            .pathMatchers("/rest/*/user/forgottenPassword/*").permitAll()
            .pathMatchers("/rest/*/pubsub/auth/recover/*").permitAll()
            .pathMatchers("/rest/*/icure/v").permitAll()
            .pathMatchers("/rest/*/icure/p").permitAll()
            .pathMatchers("/rest/*/icure/check").permitAll()
            .pathMatchers("/rest/*/icure/c").permitAll()
            .pathMatchers("/rest/*/icure/ok").permitAll()
            .pathMatchers("/rest/*/icure/pok").permitAll()
            .pathMatchers("/").permitAll()
            .pathMatchers("/ping.json").permitAll()
            .pathMatchers("/actuator/**").permitAll()
            .matchers(
                TokenWebExchangeMatcher(asyncCacheManager).paths(
                    "/rest/*/document/*/attachment/*",
                    "/rest/*/form/template/*/attachment/*",
                    "/ws/**"
                )
            ).hasRole("USER")
            .pathMatchers("/**").hasRole("USER")
            .and()
            .csrf().disable()
            .httpBasic()
            .and()
            .addFilterAfter(
                AuthenticationWebFilter(authenticationManager).apply {
                    this.setAuthenticationFailureHandler(ServerAuthenticationEntryPointFailureHandler(UnauthorizedEntryPoint()))
                    if (sessionEnabled) this.setSecurityContextRepository(object:WebSessionServerSecurityContextRepository() {
                        override fun save(exchange: ServerWebExchange, context: SecurityContext) = exchange.request.headers["X-Bypass-Session"]?.let { Mono.empty() } ?: super.save(exchange, context)
                        override fun load(exchange: ServerWebExchange) = exchange.request.headers["X-Bypass-Session"]?.let { Mono.empty() } ?: super.load(exchange)
                    })
                    else this.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance())

                    // TODO: When SESSION ID will be dismissed, change it back to JwtAuthenticationConverter
                    this.setServerAuthenticationConverter { exchange ->
                        // First I check for the JWT Header
                        exchange?.request?.headers?.get("Authorization")
                            ?.filterNotNull()
                            ?.firstOrNull { it.contains("Bearer") }
                            ?.let {
                                Mono.just(EncodedJwtAuthenticationToken(encodedJwt = it.replace("Bearer ", "")))
                            } ?: if (sessionEnabled) exchange.session.flatMap { webSession -> //Otherwise, I check the session
                            ServerHttpBasicAuthenticationConverter().convert(exchange).flatMap { auth ->
                                //Ignore basic auth if SPRING_SECURITY_CONTEXT was loaded from session
                                webSession.attributes[WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME]?.let {
                                    (it as? SecurityContext)?.let { context ->
                                        if (context.authentication.principal != auth.principal) Mono.just(auth)
                                        else Mono.empty()
                                    }
                                } ?: Mono.just(auth)
                            }
                        }
                        else Mono.empty()
                    }
                },
                SecurityWebFiltersOrder.REACTOR_CONTEXT
            )
            .build()
    }
}

private fun ServerWebExchangeMatcher.and(matcher: ServerWebExchangeMatcher): ServerWebExchangeMatcher = AndServerWebExchangeMatcher(this, matcher)
private fun ServerWebExchangeMatcher.paths(vararg antPatterns: String): ServerWebExchangeMatcher = AndServerWebExchangeMatcher(this, ServerWebExchangeMatchers.pathMatchers(*antPatterns))
