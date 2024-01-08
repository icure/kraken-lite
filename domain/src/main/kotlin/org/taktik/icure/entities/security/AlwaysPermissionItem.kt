package org.taktik.icure.entities.security

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.domain.filter.impl.predicate.AlwaysPredicate

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class AlwaysPermissionItem(override val type: PermissionType) : PermissionItem {
	override val predicate = AlwaysPredicate()
	override fun merge(other: PermissionItem) = this
}
