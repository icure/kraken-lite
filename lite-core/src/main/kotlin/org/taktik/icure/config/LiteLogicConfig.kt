/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.config

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asyncdao.AgendaDAO
import org.taktik.icure.asyncdao.ApplicationSettingsDAO
import org.taktik.icure.asyncdao.CalendarItemDAO
import org.taktik.icure.asyncdao.CalendarItemTypeDAO
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
import org.taktik.icure.asyncdao.PlaceDAO
import org.taktik.icure.asyncdao.ReceiptDAO
import org.taktik.icure.asyncdao.SecureDelegationKeyMapDAO
import org.taktik.icure.asyncdao.TarificationDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.AgendaLogic
import org.taktik.icure.asynclogic.ApplicationSettingsLogic
import org.taktik.icure.asynclogic.CalendarItemLogic
import org.taktik.icure.asynclogic.CalendarItemTypeLogic
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
import org.taktik.icure.asynclogic.PlaceLogic
import org.taktik.icure.asynclogic.ReceiptLogic
import org.taktik.icure.asynclogic.SecureDelegationKeyMapLogic
import org.taktik.icure.asynclogic.SessionInformationProvider
import org.taktik.icure.asynclogic.TarificationLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.datastore.DatastoreInstanceProvider
import org.taktik.icure.asynclogic.impl.AgendaLogicImpl
import org.taktik.icure.asynclogic.impl.ApplicationSettingsLogicImpl
import org.taktik.icure.asynclogic.impl.CalendarItemLogicImpl
import org.taktik.icure.asynclogic.impl.CalendarItemTypeLogicImpl
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
import org.taktik.icure.asynclogic.impl.PlaceLogicImpl
import org.taktik.icure.asynclogic.impl.ReceiptLogicImpl
import org.taktik.icure.asynclogic.impl.SecureDelegationKeyMapLogicImpl
import org.taktik.icure.asynclogic.impl.SessionInformationProviderImpl
import org.taktik.icure.asynclogic.impl.TarificationLogicImpl
import org.taktik.icure.asynclogic.impl.UserLogicImpl
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.asynclogic.objectstorage.DocumentDataAttachmentLoader
import org.taktik.icure.asynclogic.objectstorage.DocumentDataAttachmentModificationLogic
import org.taktik.icure.asynclogic.objectstorage.ReceiptDataAttachmentLoader
import org.taktik.icure.entities.Agenda
import org.taktik.icure.entities.CalendarItem
import org.taktik.icure.entities.CalendarItemType
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Device
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Insurance
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.Place
import org.taktik.icure.entities.Receipt
import org.taktik.icure.entities.User
import org.taktik.icure.entities.base.Code
import org.taktik.icure.mergers.Merger
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
		liteConfig: LiteCardinalVersionConfig
	): Fixer = FixerImpl(
		fixedValueProvider = CommonFixedValueProvider(
			dataOwnerProvider = sessionInformationProvider
		),
		forceSkipLegacyFixing = liteConfig.isConfiguredForCardinalModel()
	)

	@Bean
	fun userLogic(
		datastoreInstanceProvider: DatastoreInstanceProvider,
		userDAO: UserDAO,
		secretValidator: SecretValidator,
		filters: Filters,
		cloudUserEnhancer: UserEnhancer,
		fixer: Fixer,
		@Qualifier("userMerger") merger: Merger<User>,
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
			override suspend fun tryPurge(userIds: List<IdAndRev>) { }
		},
		userMerger = merger
	)

	@Bean
	fun insuranceLogic(
		insuranceDAO: InsuranceDAO,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer,
		filters: Filters,
		@Qualifier("insuranceMerger") merger: Merger<Insurance>,
	): InsuranceLogic = InsuranceLogicImpl(
		insuranceDAO = insuranceDAO,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		filters = filters,
		merger = merger
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
		datastoreInstanceProvider: DatastoreInstanceProvider,
		@Qualifier("codeMerger") merger: Merger<Code>,
	): CodeLogic = CodeLogicImpl(
		codeDAO = codeDAO,
		filters = filters,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		codeMerger = merger
	)

	@Bean
	fun calendarItemTypeLogic(
		calendarItemTypeDAO: CalendarItemTypeDAO,
		filters: Filters,
		fixer: Fixer,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		@Qualifier("calendarItemTypeMerger") merger: Merger<CalendarItemType>,
	): CalendarItemTypeLogic = CalendarItemTypeLogicImpl(
		calendarItemTypeDAO = calendarItemTypeDAO,
		filters = filters,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		merger = merger
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
		fixer: Fixer,
		@Qualifier("contactMerger") merger: Merger<Contact>,
	): ContactLogic = ContactLogicImpl(
		contactDAO = contactDAO,
		exchangeDataMapLogic = exchangeDataMapLogic,
		sessionLogic = sessionLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		filters = filters,
		fixer = fixer,
		contactMerger = merger
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
		@Qualifier("documentMerger") merger: Merger<Document>,
	): DocumentLogic = DocumentLogicImpl(
		documentDAO = documentDAO,
		sessionLogic = sessionLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		exchangeDataMapLogic = exchangeDataMapLogic,
		attachmentModificationLogic = attachmentModificationLogic,
		attachmentLoader = attachmentLoader,
		fixer = fixer,
		filters = filters,
		documentMerger = merger
	)

	@Bean
	fun formLogic(
		formDAO: FormDAO,
		exchangeDataMapLogic: ExchangeDataMapLogic,
		sessionLogic: SessionInformationProvider,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer,
		filters: Filters,
		@Qualifier("formMerger") merger: Merger<Form>,
	): FormLogic = FormLogicImpl(
		formDAO = formDAO,
		exchangeDataMapLogic = exchangeDataMapLogic,
		sessionLogic = sessionLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		filters = filters,
		formMerger = merger
	)

	@Bean
	fun healthElementLogic(
		healthElementDAO: HealthElementDAO,
		filters: Filters,
		sessionLogic: SessionInformationProvider,
		exchangeDataMapLogic: ExchangeDataMapLogic,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer,
		@Qualifier("healthElementMerger") merger: Merger<HealthElement>,
	): HealthElementLogic = HealthElementLogicImpl(
		filters = filters,
		healthElementDAO = healthElementDAO,
		sessionLogic = sessionLogic,
		exchangeDataMapLogic = exchangeDataMapLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		healthElementMerger = merger
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
		fixer: Fixer,
		@Qualifier("invoiceMerger") merger: Merger<Invoice>,
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
		fixer = fixer,
		invoiceMerger = merger
	)

	@Bean
	fun messageLogic(
		messageDAO: MessageDAO,
		exchangeDataMapLogic: ExchangeDataMapLogic,
		sessionLogic: SessionInformationProvider,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		filters: Filters,
		userLogic: UserLogic,
		fixer: Fixer,
		@Qualifier("messageMerger") merger: Merger<Message>,
	): MessageLogic = MessageLogicImpl(
		messageDAO = messageDAO,
		exchangeDataMapLogic = exchangeDataMapLogic,
		sessionLogic = sessionLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		filters = filters,
		userLogic = userLogic,
		fixer = fixer,
		messageMerger = merger
	)

	@Bean
	fun patientLogic(
		sessionLogic: SessionInformationProvider,
		patientDAO: PatientDAO,
		filters: Filters,
		exchangeDataMapLogic: ExchangeDataMapLogic,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer,
		@Qualifier("patientMerger") merger: Merger<Patient>,
	): PatientLogic = PatientLogicImpl(
		sessionLogic = sessionLogic,
		patientDAO = patientDAO,
		filters = filters,
		exchangeDataMapLogic = exchangeDataMapLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		patientMerger = merger
	)

	@Bean
	fun placeLogic(
		placeDAO: PlaceDAO,
		filters: Filters,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer,
		@Qualifier("placeMerger") merger: Merger<Place>,
	): PlaceLogic = PlaceLogicImpl(
		placeDAO = placeDAO,
		filters = filters,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		merger = merger
	)

	@Bean
	fun healthcarePartyLogic(
		filters: Filters,
		healthcarePartyDAO: HealthcarePartyDAO,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer,
		@Qualifier("healthcarePartyMerger") merger: Merger<HealthcareParty>,
	): HealthcarePartyLogic = HealthcarePartyLogicImpl(
		filters = filters,
		healthcarePartyDAO = healthcarePartyDAO,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		merger = merger
	)

	@Bean
	fun deviceLogic(
		filters: Filters,
		deviceDAO: DeviceDAO,
		datastoreInstanceProvider: DatastoreInstanceProvider,
		fixer: Fixer,
		@Qualifier("deviceMerger") merger: Merger<Device>,
	): DeviceLogic = DeviceLogicImpl(
		filters = filters,
		deviceDAO = deviceDAO,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		merger = merger
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
		filters: Filters,
		@Qualifier("agendaMerger") merger: Merger<Agenda>,
	): AgendaLogic = AgendaLogicImpl(
		agendaDAO = agendaDAO,
		sdkVersionConfig = sdkVersionConfig,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		filters = filters,
		merger = merger
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
		filters: Filters,
		@Qualifier("calendarItemMerger") merger: Merger<CalendarItem>,
		cardinalVersionConfig: CardinalVersionConfig
	): CalendarItemLogic = CalendarItemLogicImpl(
		calendarItemDAO = calendarItemDAO,
		userDAO = userDAO,
		agendaLogic = agendaLogic,
		exchangeDataMapLogic = exchangeDataMapLogic,
		sessionLogic = sessionLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		filters = filters,
		merger = merger,
		cardinalVersionConfig = cardinalVersionConfig
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
		filters: Filters,
		@Qualifier("receiptMerger") merger: Merger<Receipt>,
		attachmentLoader: ReceiptDataAttachmentLoader,
	): ReceiptLogic = ReceiptLogicImpl(
		receiptDAO = receiptDAO,
		exchangeDataMapLogic = exchangeDataMapLogic,
		sessionLogic = sessionLogic,
		datastoreInstanceProvider = datastoreInstanceProvider,
		fixer = fixer,
		filters = filters,
		merger = merger,
		attachmentLoader = attachmentLoader
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
