/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asyncdao.CodeDAO
import org.taktik.icure.asyncdao.ContactDAO
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asyncdao.FormDAO
import org.taktik.icure.asyncdao.HealthElementDAO
import org.taktik.icure.asyncdao.HealthcarePartyDAO
import org.taktik.icure.asyncdao.InsuranceDAO
import org.taktik.icure.asyncdao.InvoiceDAO
import org.taktik.icure.asyncdao.MessageDAO
import org.taktik.icure.asyncdao.PatientDAO
import org.taktik.icure.asyncdao.TarificationDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.EntityReferenceLogic
import org.taktik.icure.asynclogic.ExchangeDataMapLogic
import org.taktik.icure.asynclogic.InsuranceLogic
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.datastore.DatastoreInstanceProvider
import org.taktik.icure.asynclogic.impl.CodeLogicImpl
import org.taktik.icure.asynclogic.impl.ContactLogicImpl
import org.taktik.icure.asynclogic.impl.DocumentLogicImpl
import org.taktik.icure.asynclogic.impl.FormLogicImpl
import org.taktik.icure.asynclogic.impl.HealthElementLogicImpl
import org.taktik.icure.asynclogic.impl.InsuranceLogicImpl
import org.taktik.icure.asynclogic.impl.InvoiceLogicImpl
import org.taktik.icure.asynclogic.impl.MessageLogicImpl
import org.taktik.icure.asynclogic.impl.PatientLogicImpl
import org.taktik.icure.asynclogic.impl.SessionInformationProviderImpl
import org.taktik.icure.asynclogic.impl.TarificationLogicImpl
import org.taktik.icure.asynclogic.impl.UserLogicImpl
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.asynclogic.objectstorage.DocumentDataAttachmentLoader
import org.taktik.icure.asynclogic.objectstorage.DocumentDataAttachmentModificationLogic
import org.taktik.icure.security.SessionAccessControlKeysProvider
import org.taktik.icure.security.credentials.SecretValidator
import org.taktik.icure.security.user.UserEnhancer
import org.taktik.icure.validation.aspect.Fixer

@Configuration
class LiteLogicConfig {
    @Bean
    fun sessionInformationProvider(
        sessionAccessControlKeysProvider: SessionAccessControlKeysProvider,
    ): SessionInformationProvider = SessionInformationProviderImpl(
        sessionAccessControlKeysProvider,
    )

    @Bean
    fun userLogic(
        datastoreInstanceProvider: DatastoreInstanceProvider,
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
        filters: Filters
    ) = InsuranceLogicImpl(
        insuranceDAO,
        datastoreInstanceProvider,
        fixer,
        filters
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
        datastoreInstanceProvider: DatastoreInstanceProvider,
        filters: Filters
    ) = TarificationLogicImpl(
        tarificationDAO,
        datastoreInstanceProvider,
        fixer,
        filters
    )

    @Bean
    fun contactLogic(
        contactDAO: ContactDAO,
        exchangeDataMapLogic: ExchangeDataMapLogic,
        sessionLogic: SessionInformationProvider,
        datastoreInstanceProvider: DatastoreInstanceProvider,
        filters: Filters,
        fixer: Fixer
    ) = ContactLogicImpl(contactDAO, exchangeDataMapLogic, sessionLogic, datastoreInstanceProvider, filters, fixer)

    @Bean
    fun documentLogic(
        documentDAO: DocumentDAO,
        sessionLogic: SessionInformationProvider,
        datastoreInstanceProvider: DatastoreInstanceProvider,
        exchangeDataMapLogic: ExchangeDataMapLogic,
        attachmentModificationLogic: DocumentDataAttachmentModificationLogic,
        @Qualifier("documentDataAttachmentLoader") attachmentLoader: DocumentDataAttachmentLoader,
        fixer: Fixer,
        filters: Filters,
    ) = DocumentLogicImpl(
        documentDAO,
        sessionLogic,
        datastoreInstanceProvider,
        exchangeDataMapLogic,
        attachmentModificationLogic,
        attachmentLoader,
        fixer,
        filters
    )

    @Bean
    fun formLogic(
        formDAO: FormDAO,
        exchangeDataMapLogic: ExchangeDataMapLogic,
        sessionLogic: SessionInformationProvider,
        datastoreInstanceProvider: DatastoreInstanceProvider,
        fixer: Fixer,
        filters: Filters,
    ) = FormLogicImpl(formDAO, exchangeDataMapLogic, sessionLogic, datastoreInstanceProvider, fixer, filters)

    @Bean
    fun healthElementLogic(
        healthElementDAO: HealthElementDAO,
        filters: Filters,
        sessionLogic: SessionInformationProvider,
        exchangeDataMapLogic: ExchangeDataMapLogic,
        datastoreInstanceProvider: DatastoreInstanceProvider,
        fixer: Fixer
    ) = HealthElementLogicImpl(filters, healthElementDAO, sessionLogic, exchangeDataMapLogic, datastoreInstanceProvider, fixer)

    @Bean
    fun invoiceLogic(
        filters: Filters,
        userLogic: UserLogic,
        insuranceLogic: InsuranceLogic,
        uuidGenerator: UUIDGenerator,
        entityReferenceLogic: EntityReferenceLogic,
        invoiceDAO: InvoiceDAO,
        sessionLogic: SessionInformationProvider,
        exchangeDataMapLogic: ExchangeDataMapLogic,
        datastoreInstanceProvider: DatastoreInstanceProvider,
        fixer: Fixer
    ) = InvoiceLogicImpl(filters, userLogic, insuranceLogic, uuidGenerator, entityReferenceLogic, invoiceDAO, sessionLogic, exchangeDataMapLogic, datastoreInstanceProvider, fixer)

    @Bean
    fun messageLogic(
        messageDAO: MessageDAO,
        exchangeDataMapLogic: ExchangeDataMapLogic,
        sessionLogic: SessionInformationProvider,
        datastoreInstanceProvider: DatastoreInstanceProvider,
        filters: Filters,
        userLogic: UserLogic,
        fixer: Fixer
    ) = MessageLogicImpl(messageDAO, exchangeDataMapLogic, sessionLogic, datastoreInstanceProvider, filters, userLogic, fixer)

    @Bean
    fun patientLogic(
        healthcarePartyDAO: HealthcarePartyDAO,
        sessionLogic: SessionInformationProvider,
        patientDAO: PatientDAO,
        userLogic: UserLogic,
        filters: Filters,
        exchangeDataMapLogic: ExchangeDataMapLogic,
        datastoreInstanceProvider: DatastoreInstanceProvider,
        fixer: Fixer
    ) = PatientLogicImpl(sessionLogic, patientDAO, userLogic, filters, exchangeDataMapLogic, datastoreInstanceProvider, fixer)
}
