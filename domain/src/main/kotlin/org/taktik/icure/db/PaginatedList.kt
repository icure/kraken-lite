/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.db

data class PaginatedList<T>(val pageSize: Int, val totalSize: Int, var rows: List<T>, val nextKeyPair: PaginatedDocumentKeyIdPair?)
