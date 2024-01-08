/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.exceptions

import org.taktik.couchdb.exception.UpdateConflictException
import org.taktik.icure.entities.base.StoredDocument

class BulkUpdateConflictException(
    var conflicts: List<UpdateConflictException>,
    var savedDocuments: List<StoredDocument?>
) : PersistenceException()
