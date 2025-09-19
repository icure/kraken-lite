/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.config

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asyncdao.AgendaDAO
import org.taktik.icure.asyncdao.ApplicationSettingsDAO
import org.taktik.icure.asyncdao.CalendarItemDAO
import org.taktik.icure.asyncdao.ClassificationDAO
import org.taktik.icure.asyncdao.CodeDAO
import org.taktik.icure.asyncdao.ContactDAO
import org.taktik.icure.asyncdao.DeviceDAO
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asyncdao.EntityInfoDAO
import org.taktik.icure.asyncdao.ExchangeDataDAO
import org.taktik.icure.asyncdao.ExchangeDataMapDAO
import org.taktik.icure.asyncdao.FormDAO
import org.taktik.icure.asyncdao.HealthElementDAO
import org.taktik.icure.asyncdao.HealthcarePartyDAO
import org.taktik.icure.asyncdao.InsuranceDAO
import org.taktik.icure.asyncdao.InvoiceDAO
import org.taktik.icure.asyncdao.MaintenanceTaskDAO
import org.taktik.icure.asyncdao.MedicalLocationDAO
import org.taktik.icure.asyncdao.MessageDAO
import org.taktik.icure.asyncdao.PatientDAO
import org.taktik.icure.asyncdao.ReceiptDAO
import org.taktik.icure.asyncdao.SecureDelegationKeyMapDAO
import org.taktik.icure.asyncdao.TarificationDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.AgendaLogic
import org.taktik.icure.asynclogic.ApplicationSettingsLogic
import org.taktik.icure.asynclogic.CalendarItemLogic
import org.taktik.icure.asynclogic.ClassificationLogic
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.DataOwnerLogic
import org.taktik.icure.asynclogic.DeviceLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.EntityReferenceLogic
import org.taktik.icure.asynclogic.ExchangeDataLogic
import org.taktik.icure.asynclogic.ExchangeDataMapLogic
import org.taktik.icure.asynclogic.FormLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.InsuranceLogic
import org.taktik.icure.asynclogic.InvoiceLogic
import org.taktik.icure.asynclogic.MaintenanceTaskLogic
import org.taktik.icure.asynclogic.MedicalLocationLogic
import org.taktik.icure.asynclogic.MessageLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.ReceiptLogic
import org.taktik.icure.asynclogic.SecureDelegationKeyMapLogic
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.asynclogic.TarificationLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.datastore.DatastoreInstanceProvider
import org.taktik.icure.asynclogic.impl.AgendaLogicImpl
import org.taktik.icure.asynclogic.impl.ApplicationSettingsLogicImpl
import org.taktik.icure.asynclogic.impl.CalendarItemLogicImpl
import org.taktik.icure.asynclogic.impl.ClassificationLogicImpl
import org.taktik.icure.asynclogic.impl.CodeLogicImpl
import org.taktik.icure.asynclogic.impl.ContactLogicImpl
import org.taktik.icure.asynclogic.impl.DataOwnerLogicImpl
import org.taktik.icure.asynclogic.impl.DeviceLogicImpl
import org.taktik.icure.asynclogic.impl.DocumentLogicImpl
import org.taktik.icure.asynclogic.impl.ExchangeDataLogicImpl
import org.taktik.icure.asynclogic.impl.ExchangeDataMapLogicImpl
import org.taktik.icure.asynclogic.impl.FixerImpl
import org.taktik.icure.asynclogic.impl.FormLogicImpl
import org.taktik.icure.asynclogic.impl.HealthElementLogicImpl
import org.taktik.icure.asynclogic.impl.HealthcarePartyLogicImpl
import org.taktik.icure.asynclogic.impl.InsuranceLogicImpl
import org.taktik.icure.asynclogic.impl.InvoiceLogicImpl
import org.taktik.icure.asynclogic.impl.MaintenanceTaskLogicImpl
import org.taktik.icure.asynclogic.impl.MedicalLocationLogicImpl
import org.taktik.icure.asynclogic.impl.MessageLogicImpl
import org.taktik.icure.asynclogic.impl.PatientLogicImpl
import org.taktik.icure.asynclogic.impl.ReceiptLogicImpl
import org.taktik.icure.asynclogic.impl.SecureDelegationKeyMapLogicImpl
import org.taktik.icure.asynclogic.impl.SessionInformationProviderImpl
import org.taktik.icure.asynclogic.impl.TarificationLogicImpl
import org.taktik.icure.asynclogic.impl.UserLogicImpl
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.asynclogic.objectstorage.DocumentDataAttachmentLoader
import org.taktik.icure.asynclogic.objectstorage.DocumentDataAttachmentModificationLogic
import org.taktik.icure.entities.User
import org.taktik.icure.security.SessionAccessControlKeysProvider
import org.taktik.icure.security.credentials.SecretValidator
import org.taktik.icure.security.user.GlobalUserUpdater
import org.taktik.icure.security.user.UserEnhancer
import org.taktik.icure.validation.DataOwnerProvider
import org.taktik.icure.validation.aspect.CommonFixedValueProvider
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
	fun liteFixer(
		sessionInformationProvider: SessionInformationProvider,
	): Fixer = FixerImpl(
		fixedValueProvider = CommonFixedValueProvider(
			dataOwnerProvider = sessionInformationProvider
		)
	)

	@Bean
	fun userLogic(
		datastoreInstanceProvider: DatastoreInstanceProvider,
		userDAO: UserDAO,
		secretValidator: SecretValidator,
		filters: Filters,
		cloudUserEnhancer: UserEnhancer,
		fixer: Fixer
	): UserLogic = UserLogicImpl(
		datastoreInstanceProvider = datastoreInstanceProvider,
		filters = filters,
		userDAO = userDAO,
		secretValidator = secretValidator,
		userEnhancer = cloudUserEnhancer,
		fixer = fixer,
		globalUserUpdater = object : GlobalUserUpdater {
			override suspend fun tryUpdate(updatedUser: User): User = updatedUser
			override fun tryingUpdates(updatedUsers: Flow<User>): Flow<User> = updatedUsers
			override suspend fun tryPurge(localId: String, localRev: String) { }
		}
	)

	@Bean
	fun insuranceLogic(
		insuranceDAO: InsuranceDAO,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer,
		filters: Filters
	): InsuranceLogic = InsuranceLogicImpl(
		insuranceDAO = insuranceDAO,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		filters = filters
	)

	@Bean
	fun applicationSettingsLogic(
		applicationSettingsDAO: ApplicationSettingsDAO,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		sessionInformationProvider: SessionInformationProvider,
		exchangeDataMapLogic: ExchangeDataMapLogic,
		fixer: Fixer,
		filters: Filters,
	): ApplicationSettingsLogic = ApplicationSettingsLogicImpl(
		applicationSettingsDAO = applicationSettingsDAO,
		datastoreInstanceProvider = datastoreInstanceProvider,
		sessionInformationProvider = sessionInformationProvider,
		exchangeDataMapLogic = exchangeDataMapLogic,
		fixer = fixer,
		filters = filters
	)

	@Bean
	fun codeLogic(
		codeDAO: CodeDAO,
		filters: Filters,
		fixer: Fixer,
		datastoreInstanceProvider: DatastoreInstanceProvider
	): CodeLogic = CodeLogicImpl(
		codeDAO = codeDAO,
		filters = filters,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer
	)

	@Bean
	fun tarificationLogic(
		tarificationDAO: TarificationDAO,
		fixer: Fixer,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		filters: Filters
	): TarificationLogic = TarificationLogicImpl(
		tarificationDAO = tarificationDAO,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		filters = filters
	)

	@Bean
	fun exchangeDataMapLogic(
		exchangeDataMapDAO: ExchangeDataMapDAO,
		datastoreInstanceProvider: DatastoreInstanceProvider,
	): ExchangeDataMapLogic = ExchangeDataMapLogicImpl(
		exchangeDataMapDAO = exchangeDataMapDAO,
		datastoreInstanceProvider = datastoreInstanceProvider,
	)

	@Bean
	fun contactLogic(
		contactDAO: ContactDAO,
		exchangeDataMapLogic: ExchangeDataMapLogic,
		sessionLogic: SessionInformationProvider,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		filters: Filters,
		fixer: Fixer
	): ContactLogic = ContactLogicImpl(
		contactDAO = contactDAO,
		exchangeDataMapLogic = exchangeDataMapLogic,
		sessionLogic = sessionLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		filters = filters,
		fixer = fixer
	)

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
	): DocumentLogic = DocumentLogicImpl(
		documentDAO = documentDAO,
		sessionLogic = sessionLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		exchangeDataMapLogic = exchangeDataMapLogic,
		attachmentModificationLogic = attachmentModificationLogic,
		attachmentLoader = attachmentLoader,
		fixer = fixer,
		filters = filters
	)

	@Bean
	fun formLogic(
		formDAO: FormDAO,
		exchangeDataMapLogic: ExchangeDataMapLogic,
		sessionLogic: SessionInformationProvider,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer,
		filters: Filters,
	): FormLogic = FormLogicImpl(
		formDAO = formDAO,
		exchangeDataMapLogic = exchangeDataMapLogic,
		sessionLogic = sessionLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		filters = filters
	)

	@Bean
	fun healthElementLogic(
		healthElementDAO: HealthElementDAO,
		filters: Filters,
		sessionLogic: SessionInformationProvider,
		exchangeDataMapLogic: ExchangeDataMapLogic,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer
	): HealthElementLogic = HealthElementLogicImpl(
		filters = filters,
		healthElementDAO = healthElementDAO,
		sessionLogic = sessionLogic,
		exchangeDataMapLogic = exchangeDataMapLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer
	)

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
	): InvoiceLogic = InvoiceLogicImpl(
		filters = filters,
		userLogic = userLogic,
		insuranceLogic = insuranceLogic,
		uuidGenerator = uuidGenerator,
		entityReferenceLogic = entityReferenceLogic,
		invoiceDAO = invoiceDAO,
		sessionLogic = sessionLogic,
		exchangeDataMapLogic = exchangeDataMapLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer
	)

	@Bean
	fun messageLogic(
		messageDAO: MessageDAO,
		exchangeDataMapLogic: ExchangeDataMapLogic,
		sessionLogic: SessionInformationProvider,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		filters: Filters,
		userLogic: UserLogic,
		fixer: Fixer
	): MessageLogic = MessageLogicImpl(
		messageDAO = messageDAO,
		exchangeDataMapLogic = exchangeDataMapLogic,
		sessionLogic = sessionLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		filters = filters,
		userLogic = userLogic,
		fixer = fixer
	)

	@Bean
	fun patientLogic(
		sessionLogic: SessionInformationProvider,
		patientDAO: PatientDAO,
		filters: Filters,
		exchangeDataMapLogic: ExchangeDataMapLogic,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer
	): PatientLogic = PatientLogicImpl(
		sessionLogic = sessionLogic,
		patientDAO = patientDAO,
		filters = filters,
		exchangeDataMapLogic = exchangeDataMapLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer
	)

	@Bean
	fun healthcarePartyLogic(
		filters: Filters,
		healthcarePartyDAO: HealthcarePartyDAO,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer
	): HealthcarePartyLogic = HealthcarePartyLogicImpl(
		filters = filters,
		healthcarePartyDAO = healthcarePartyDAO,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer
	)

	@Bean
	fun deviceLogic(
		filters: Filters,
		deviceDAO: DeviceDAO,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer
	): DeviceLogic = DeviceLogicImpl(
		filters = filters,
		deviceDAO = deviceDAO,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer
	)

	@Bean
	fun medicalLocationLogic(
		filters: Filters,
		medicalLocationDAO: MedicalLocationDAO,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer
	): MedicalLocationLogic = MedicalLocationLogicImpl(
		filters = filters,
		medicalLocationDAO = medicalLocationDAO,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer
	)

	@Bean
	fun agendaLogic(
		agendaDAO: AgendaDAO,
		sdkVersionConfig: SdkVersionConfig,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer,
		filters: Filters
	): AgendaLogic = AgendaLogicImpl(
		agendaDAO = agendaDAO,
		sdkVersionConfig = sdkVersionConfig,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		filters = filters
	)


	@Bean
	fun calendarItemLogic(
		calendarItemDAO: CalendarItemDAO,
		userDAO: UserDAO,
		agendaLogic: AgendaLogic,
		exchangeDataMapLogic: ExchangeDataMapLogic,
		sessionLogic: SessionInformationProvider,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer,
		filters: Filters
	): CalendarItemLogic = CalendarItemLogicImpl(
		calendarItemDAO = calendarItemDAO,
		userDAO = userDAO,
		agendaLogic = agendaLogic,
		exchangeDataMapLogic = exchangeDataMapLogic,
		sessionLogic = sessionLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		filters = filters
	)

	@Bean
	fun classificationLogic(
		classificationDAO: ClassificationDAO,
		exchangeDataMapLogic: ExchangeDataMapLogic,
		sessionLogic: SessionInformationProvider,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer,
		filters: Filters
	): ClassificationLogic = ClassificationLogicImpl(
		classificationDAO = classificationDAO,
		exchangeDataMapLogic = exchangeDataMapLogic,
		sessionLogic = sessionLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		filters = filters
	)

	@Bean
	fun dataOwnerLogic(
		patientDAO: PatientDAO,
		healthcarePartyDAO: HealthcarePartyDAO,
		deviceDAO: DeviceDAO,
		datastoreInstanceProvider: DatastoreInstanceProvider
	): DataOwnerLogic = DataOwnerLogicImpl(
		patientDao = patientDAO,
		hcpDao = healthcarePartyDAO,
		deviceDao = deviceDAO,
		datastoreInstanceProvider = datastoreInstanceProvider
	)

	@Bean
	fun exchangeDataLogic(
		exchangeDataDAO: ExchangeDataDAO,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		@Qualifier("baseEntityInfoDao") baseEntityInfoDao: EntityInfoDAO,
		@Qualifier("patientEntityInfoDao") patientEntityInfoDao: EntityInfoDAO,
		objectMapper: ObjectMapper,
		dataOwnerProvider: DataOwnerProvider
	): ExchangeDataLogic = ExchangeDataLogicImpl(
		exchangeDataDAO = exchangeDataDAO,
		datastoreInstanceProvider = datastoreInstanceProvider,
		baseEntityInfoDao = baseEntityInfoDao,
		patientEntityInfoDao = patientEntityInfoDao,
		objectMapper = objectMapper,
		dataOwnerProvider = dataOwnerProvider
	)

	@Bean
	fun maintenanceTaskLogic(
		maintenanceTaskDAO: MaintenanceTaskDAO,
		exchangeDataMapLogic: ExchangeDataMapLogic,
		sessionLogic: SessionInformationProvider,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer,
		filters: Filters
	): MaintenanceTaskLogic = MaintenanceTaskLogicImpl(
		maintenanceTaskDAO = maintenanceTaskDAO,
		exchangeDataMapLogic = exchangeDataMapLogic,
		sessionLogic = sessionLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		filters = filters
	)

	@Bean
	fun receiptLogic(
		receiptDAO: ReceiptDAO,
		exchangeDataMapLogic: ExchangeDataMapLogic,
		sessionLogic: SessionInformationProvider,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer,
		filters: Filters
	): ReceiptLogic = ReceiptLogicImpl(
		receiptDAO = receiptDAO,
		exchangeDataMapLogic = exchangeDataMapLogic,
		sessionLogic = sessionLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		filters = filters
	)

	@Bean
	fun secureDelegationKeyMapLogic(
		secureDelegationKeyMapDAO: SecureDelegationKeyMapDAO,
		exchangeDataMapLogic: ExchangeDataMapLogic,
		sessionLogic: SessionInformationProvider,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer,
		filters: Filters
	): SecureDelegationKeyMapLogic = SecureDelegationKeyMapLogicImpl(
		secureDelegationKeyMapDAO = secureDelegationKeyMapDAO,
		exchangeDataMapLogic = exchangeDataMapLogic,
		sessionLogic = sessionLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		filters = filters
	)
}
