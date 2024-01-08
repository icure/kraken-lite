/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.dao.migration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
class MigrationStub : Serializable {
    @JsonProperty("_id")
    var id: String? = null

    @JsonProperty("_rev")
    var rev: String? = null
    var timestamp: Long? = null

    constructor()
    constructor(id: String?) {
        this.id = id
        timestamp = Instant.now().toEpochMilli()
    }
}
