package org.taktik.icure.services.external.rest.v1.dto

import java.io.Serializable

data class ApiUsageReportDto(
    val lastResetTimestamp: Long?,
    val report: Map<String, Long>
): Serializable