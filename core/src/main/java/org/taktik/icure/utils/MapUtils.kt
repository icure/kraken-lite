/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.utils

/**
 * Created by aduchate on 02/10/11, 16:34
 */
object MapUtils {
    fun <K, V> hashMap(key: K, value: V, vararg others: Any): HashMap<K, V> {
        val result = HashMap<K, V>(others.size / 2 + 1)
        result[key] = value
        for (i in 0 until others.size / 2) {
            result[others[i * 2] as K] = others[i * 2 + 1] as V
        }
        return result
    }
}
