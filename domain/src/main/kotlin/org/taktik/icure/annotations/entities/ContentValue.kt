package org.taktik.icure.annotations.entities

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ContentValue(val contentValue: ContentValues)
