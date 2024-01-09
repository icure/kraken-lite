/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.taktik.icure.asyncdao.CodeDAO
import org.taktik.icure.asyncdao.InsuranceDAO
import org.taktik.icure.asyncdao.TarificationDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.datastore.DatastoreInstanceProvider
import org.taktik.icure.asynclogic.impl.CodeLogicImpl
import org.taktik.icure.asynclogic.impl.InsuranceLogicImpl
import org.taktik.icure.asynclogic.impl.SessionInformationProviderImpl
import org.taktik.icure.asynclogic.impl.TarificationLogicImpl
import org.taktik.icure.asynclogic.impl.UserLogicImpl
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.properties.CouchDbPropertiesImpl
import org.taktik.icure.security.SessionAccessControlKeysProvider
import org.taktik.icure.security.credentials.SecretValidator
import org.taktik.icure.security.user.UserEnhancer
import org.taktik.icure.validation.aspect.Fixer

@Configuration
class LiteLogicConfig(

){
    @Bean
    fun sessionInformationProvider(
        sessionAccessControlKeysProvider: SessionAccessControlKeysProvider,
    ): SessionInformationProvider = SessionInformationProviderImpl(
        sessionAccessControlKeysProvider,
    )

    @Bean
    fun userLogic(
        datastoreInstanceProvider: DatastoreInstanceProvider,
        couchDbProperties: CouchDbPropertiesImpl,
        userDAO: UserDAO,
        secretValidator: SecretValidator,
        filters: Filters,
        sessionLogic: SessionInformationProvider,
        cloudUserEnhancer: UserEnhancer,
        fixer: Fixer
    ): UserLogic = UserLogicImpl(
        datastoreInstanceProvider,
        filters,
        userDAO,
        secretValidator,
        cloudUserEnhancer,
        fixer
    )

    @Bean
    fun insuranceLogic(
        insuranceDAO: InsuranceDAO,
        datastoreInstanceProvider: DatastoreInstanceProvider,
        fixer: Fixer,
    ) = InsuranceLogicImpl(
        insuranceDAO,
        datastoreInstanceProvider,
        fixer
    )

    @Bean
    fun codeLogic(
        codeDAO: CodeDAO,
        filters: Filters,
        fixer: Fixer,
        datastoreInstanceProvider: DatastoreInstanceProvider
    ) = CodeLogicImpl(
        codeDAO,
        filters,
        datastoreInstanceProvider,
        fixer
    )

    @Bean
    fun tarificationLogic(
        tarificationDAO: TarificationDAO,
        fixer: Fixer,
        datastoreInstanceProvider: DatastoreInstanceProvider
    ) = TarificationLogicImpl(
        tarificationDAO,
        datastoreInstanceProvider,
        fixer
    )
}
