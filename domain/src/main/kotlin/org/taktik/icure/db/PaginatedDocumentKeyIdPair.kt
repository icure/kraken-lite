/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.db

import java.io.Serializable

/**
 * Created by emad7105 on 11/07/2014.
 */
data class PaginatedDocumentKeyIdPair(val startKey: List<String>?, val startKeyDocId: String?) : Serializable
