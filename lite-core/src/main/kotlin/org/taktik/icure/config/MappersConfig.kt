package org.taktik.icure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.services.external.rest.v1.mapper.SecureUserV1Mapper
import org.taktik.icure.services.external.rest.v1.mapper.SecureUserV1MapperImpl
import org.taktik.icure.services.external.rest.v1.mapper.UnsecureUserMapper
import org.taktik.icure.services.external.rest.v1.mapper.base.IdentifierMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.GenderMapper
import org.taktik.icure.services.external.rest.v1.mapper.filter.FilterMapper
import org.taktik.icure.services.external.rest.v1.mapper.filter.FilterMapperImpl
import org.taktik.icure.services.external.rest.v1.mapper.security.UnsecureAuthenticationTokenMapper
import org.taktik.icure.services.external.rest.v2.mapper.SecureUserV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.SecureUserV2MapperImpl
import org.taktik.icure.services.external.rest.v2.mapper.UnsecureUserV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.base.IdentifierV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.GenderV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.filter.FilterChainV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.filter.FilterV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.filter.FilterV2MapperImpl
import org.taktik.icure.services.external.rest.v2.mapper.security.UnsecureAuthenticationTokenV2Mapper

@Configuration
class MappersConfig {
    @Bean
    fun secureUserV1Mapper(
        userLogic: UserLogic,
        unsecureMapper: UnsecureUserMapper,
        unsecureTokenMapper: UnsecureAuthenticationTokenMapper
    ): SecureUserV1Mapper = SecureUserV1MapperImpl(userLogic, unsecureMapper, unsecureTokenMapper)

    @Bean
    fun secureUserV2Mapper(
        userLogic: UserLogic,
        unsecureMapper: UnsecureUserV2Mapper,
        unsecureTokenMapper: UnsecureAuthenticationTokenV2Mapper
    ): SecureUserV2Mapper = SecureUserV2MapperImpl(userLogic, unsecureMapper, unsecureTokenMapper)

    @Bean
    fun filterMapper(
        identifierMapper: IdentifierMapper,
        genderMapper: GenderMapper
    ): FilterMapper = FilterMapperImpl(identifierMapper, genderMapper)

    @Bean
    fun filterV2Mapper(
        identifierV2Mapper: IdentifierV2Mapper,
        genderV2Mapper: GenderV2Mapper
    ): FilterV2Mapper =
        FilterV2MapperImpl(identifierV2Mapper, genderV2Mapper)

    @Bean
    fun filterChainV2Mapper(filterV2Mapper: FilterV2Mapper): FilterChainV2Mapper =
        FilterChainV2Mapper(filterV2Mapper)
}
