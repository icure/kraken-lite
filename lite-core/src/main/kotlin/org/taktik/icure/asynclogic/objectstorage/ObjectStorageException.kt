package org.taktik.icure.asynclogic.objectstorage

import org.taktik.icure.exceptions.objectstorage.ObjectStorageException

/**
 * Exception thrown when the object storage service can't be reached.
 */
class UnreachableObjectStorageException(cause: Throwable?) : ObjectStorageException("Object storage service is currently unavailable.", cause)

/**
 * The requested object is currently not available, either because it is still in the process of getting stored, or because the
 * object does not exist and no-one is uploading it yet.
 */
class UnavailableObjectException(message: String, cause: Throwable?) : ObjectStorageException(message, cause)

/**
 * Exception thrown when an attempt to store some content fails because there is an issue with the local object storage.
 */
class LocalObjectStorageException(message: String, cause: Throwable?) : ObjectStorageException(message, cause)
