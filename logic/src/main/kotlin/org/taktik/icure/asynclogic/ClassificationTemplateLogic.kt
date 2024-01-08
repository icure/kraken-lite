/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.base.EntityWithSecureDelegationsLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.ClassificationTemplate
import org.taktik.icure.entities.embed.Delegation

interface ClassificationTemplateLogic : EntityPersister<ClassificationTemplate, String>, EntityWithSecureDelegationsLogic<ClassificationTemplate> {
	suspend fun createClassificationTemplate(classificationTemplate: ClassificationTemplate): ClassificationTemplate?

	suspend fun getClassificationTemplate(classificationTemplateId: String): ClassificationTemplate?
	fun deleteClassificationTemplates(ids: Set<String>): Flow<DocIdentifier>
	fun deleteClassificationTemplates(ids: Flow<String>): Flow<DocIdentifier>
	suspend fun addDelegation(classificationTemplate: ClassificationTemplate, healthcarePartyId: String, delegation: Delegation): ClassificationTemplate?

	suspend fun addDelegations(classificationTemplate: ClassificationTemplate, delegations: List<Delegation>): ClassificationTemplate?
	fun getClassificationTemplates(ids: Collection<String>): Flow<ClassificationTemplate>
	fun listClasificationsByHCPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<ClassificationTemplate>

	fun listClassificationTemplates(paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
}
