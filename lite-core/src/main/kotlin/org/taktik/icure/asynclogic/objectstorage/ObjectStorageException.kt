package org.taktik.icure.asynclogic.objectstorage

import org.taktik.icure.exceptions.ICureException

/**
 * Exception thrown if there is an error with the storage of an attachment to object storage.
 */
open class ObjectStorageException(msg: String, cause: Throwable? = null) : ICureException(msg, cause)

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
 * Exception thrown when an attempt to store some content fails because there is an object being stored or fully stored with the
 * same id but with a different content hash, or because the declared hash doesn't match the provided content.
 */
class ObjectHashConflictException(message: String, cause: Throwable?) : ObjectStorageException(message, cause)
