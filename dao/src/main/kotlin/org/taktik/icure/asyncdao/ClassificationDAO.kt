/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.entities.Classification

interface ClassificationDAO : GenericDAO<Classification> {
	fun listClassificationByPatient(datastoreInformation: IDatastoreInformation, patientId: String): Flow<Classification>

	suspend fun getClassification(datastoreInformation: IDatastoreInformation, classificationId: String): Classification?

	fun listClassificationsByHCPartyAndSecretPatientKeys(datastoreInformation: IDatastoreInformation, searchKeys: Set<String>, secretPatientKeys: List<String>): Flow<Classification>
}
