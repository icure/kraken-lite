package org.taktik.icure.asyncdao.objectstorage

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.asyncdao.InternalDAO
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask

interface ObjectStorageMigrationTasksDAO : InternalDAO<ObjectStorageMigrationTask> {
	fun <T : HasDataAttachments<T>> findTasksForEntities(entityClass: Class<T>): Flow<ObjectStorageMigrationTask>
}
