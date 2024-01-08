/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.validation.aspect

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass

@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Check(val groups: Array<KClass<*>> = [])
