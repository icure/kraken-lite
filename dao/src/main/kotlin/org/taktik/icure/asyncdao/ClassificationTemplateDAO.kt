/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.ClassificationTemplate

interface ClassificationTemplateDAO : GenericDAO<ClassificationTemplate> {
	suspend fun getClassificationTemplate(datastoreInformation: IDatastoreInformation, classificationTemplateId: String): ClassificationTemplate?

	fun listClassificationsByHCPartyAndSecretPatientKeys(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, secretPatientKeys: List<String>): Flow<ClassificationTemplate>

	fun findClassificationTemplates(datastoreInformation: IDatastoreInformation, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
}
