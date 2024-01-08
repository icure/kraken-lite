/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.entities.Document

interface DocumentDAO : GenericDAO<Document>, AttachmentManagementDAO<Document> {
	fun listConflicts(datastoreInformation: IDatastoreInformation): Flow<Document>

	fun listDocumentsByHcPartyAndSecretMessageKeys(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, secretForeignKeys: List<String>): Flow<Document>

	fun listDocumentsWithNoDelegations(datastoreInformation: IDatastoreInformation, limit: Int): Flow<Document>

	fun listDocumentsByDocumentTypeHcPartySecretMessageKeys(datastoreInformation: IDatastoreInformation, documentTypeCode: String, searchKeys: Set<String>, secretForeignKeys: List<String>): Flow<Document>

	suspend fun listDocumentsByExternalUuid(datastoreInformation: IDatastoreInformation, externalUuid: String): List<Document>
}
