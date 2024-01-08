package org.taktik.icure.entities.utils

import java.io.Serializable

data class PaginatedDocumentKeyIdPair<K>(var startKey: K? = null, var startKeyDocId: String? = null) : Serializable
