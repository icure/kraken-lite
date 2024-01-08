package org.taktik.icure.services.external.http.websocket.annotation

import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import org.springframework.web.bind.annotation.Mapping

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
annotation class WSRequestMapping(
	/**
	 * Assign a name to this mapping.
	 *
	 * **Supported at the type level as well as at the method level!**
	 * When used on both levels, a combined name is derived by concatenation
	 * with "#" as separator.
	 * @see org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
	 *
	 * @see org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy
	 */
	val name: String = "",
)
