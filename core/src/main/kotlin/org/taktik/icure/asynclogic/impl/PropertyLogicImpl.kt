/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asynclogic.impl

import org.springframework.context.annotation.Profile
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.PropertyLogic
import org.taktik.icure.constants.PropertyTypes
import org.taktik.icure.entities.base.PropertyStub
import org.taktik.icure.entities.base.PropertyTypeStub
import org.taktik.icure.entities.embed.TypedValue

@Service
@Profile("app")
class PropertyLogicImpl(private val environment: ConfigurableEnvironment) : PropertyLogic {
	private val environmentProperties: Map<String, PropertyStub> = getEnvironmentProperties().mapValues { e ->
		val propertyTypedValue = TypedValue.withValue(e.value)
		PropertyStub(
			type = PropertyTypeStub(
				identifier = e.key,
				type = propertyTypedValue?.type
			),
			typedValue = propertyTypedValue
		)
	}

	override fun getSystemProperties(includeEnvironmentProperties: Boolean): Set<PropertyStub> {
		return environmentProperties.values.toSet()
	}

	private fun getEnvironmentProperties(): Map<String, *> {
		val propertyNames = environment.propertySources.filterIsInstance(EnumerablePropertySource::class.java).flatMap { eps ->
			eps.propertyNames.map { it.replace('_', '.').toLowerCase() }.filter { it.startsWith(PropertyTypes.ENVIRONMENT_PROPERTY_PREFIX) && !it.endsWith("password") }
		}

		return propertyNames.fold(mutableMapOf<String, Any>()) { acc, propertyName ->
			val propertyKey = PropertyTypes.Category.System + propertyName.substring(PropertyTypes.ENVIRONMENT_PROPERTY_PREFIX.length)
			environment.getProperty(propertyName)?.let { propertyValue: String ->
				if ("null" == propertyValue) {
					acc
				} else if (propertyValue.trim { it <= ' ' }.toLowerCase() == "true" || propertyValue.trim { it <= ' ' }.toLowerCase() == "false") {
					acc[propertyKey] = java.lang.Boolean.valueOf(propertyValue); acc
				} else {
					try {
						acc[propertyKey] = Integer.valueOf(propertyValue)
					} catch (ignored: NumberFormatException) {
						acc[propertyKey] = propertyValue
					}
					acc
				}
			} ?: acc
		}
	}

	override fun getSystemProperty(propertyIdentifier: String): PropertyStub? {
		return environmentProperties[propertyIdentifier]
	}

	override fun <T> getSystemPropertyValue(propertyIdentifier: String): T? {
		return getSystemProperty(propertyIdentifier)?.getValue<T>()
	}
}
