package org.taktik.icure.utils

import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.chain.FilterChain

fun <T : AbstractFilter<*>> T?.orThrow(): T =
    this ?: throw IllegalArgumentException("Unsupported filter: the provided filter is not known by the backend version or is for another type of entity")

fun <T : FilterChain<*>> T?.orThrow(): T =
    this ?: throw IllegalArgumentException("Unsupported filter chain: the filter used is not known by the backend version or is for another type of entity")