/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.entities.DocumentTemplate

interface DocumentTemplateDAO : GenericDAO<DocumentTemplate>, AttachmentManagementDAO<DocumentTemplate> {
	fun listDocumentTemplatesByUserGuid(datastoreInformation: IDatastoreInformation, userId: String, guid: String?): Flow<DocumentTemplate>

	fun listDocumentTemplatesBySpecialtyAndGuid(datastoreInformation: IDatastoreInformation, healthcarePartyId: String, guid: String?): Flow<DocumentTemplate>

	fun listDocumentsByTypeUserGuid(datastoreInformation: IDatastoreInformation, documentTypeCode: String, userId: String?, guid: String?): Flow<DocumentTemplate>

	fun evictFromCache(entity: DocumentTemplate)
	suspend fun createDocumentTemplate(datastoreInformation: IDatastoreInformation, entity: DocumentTemplate): DocumentTemplate
}
