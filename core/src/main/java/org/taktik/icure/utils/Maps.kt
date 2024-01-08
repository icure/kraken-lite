/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.utils

object Maps {
    /**
     * Gets the maps at the given key.
     * If the map does not exists, create an empty HashMap, put it at the given key,
     * and returns this new empty map.
     */
    fun getMap(map: MutableMap<String, Map<*, *>>, key: String): Map<*, *>? {
        if (!map.containsKey(key)) {
            map[key] = mutableMapOf<Any, Any>()
        }
        return map[key]
    }
}
