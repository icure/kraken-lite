/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.base.EntityWithSecureDelegationsLogic
import org.taktik.icure.entities.Classification
import org.taktik.icure.entities.embed.Delegation

interface ClassificationLogic : EntityPersister<Classification, String>, EntityWithSecureDelegationsLogic<Classification> {

	suspend fun createClassification(classification: Classification): Classification?

	suspend fun getClassification(classificationId: String): Classification?
	fun listClassificationsByHCPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<Classification>
	fun deleteClassifications(ids: Collection<String>): Flow<DocIdentifier>
	fun deleteClassifications(ids: Flow<String>): Flow<DocIdentifier>

	suspend fun addDelegation(classification: Classification, healthcarePartyId: String, delegation: Delegation): Classification?
	suspend fun addDelegations(classification: Classification, delegations: List<Delegation>): Classification?
	fun getClassifications(ids: List<String>): Flow<Classification>
}
