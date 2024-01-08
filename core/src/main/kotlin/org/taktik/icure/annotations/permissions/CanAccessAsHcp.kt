package org.taktik.icure.annotations.permissions

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class CanAccessAsHcp

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class CanAccessWithHcpInHierarchy
