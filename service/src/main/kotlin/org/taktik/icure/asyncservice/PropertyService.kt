/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncservice

import org.taktik.icure.entities.base.PropertyStub

interface PropertyService {
	/**
	 * Return the system properties, those that are not linked to any roles.
	 *
	 * @return
	 */
	fun getSystemProperties(includeEnvironmentProperties: Boolean): Set<PropertyStub>

	/**
	 * Return the system property with the given identifier
	 *
	 * @param propertyIdentifier
	 * @return
	 */
	fun getSystemProperty(propertyIdentifier: String): PropertyStub?

	/**
	 * Return the system property value with the given identifier
	 *
	 * @param propertyIdentifier
	 * @return
	 */
	fun <T> getSystemPropertyValue(propertyIdentifier: String): T?

	/**
	 * Updates the system property value with the given identifier
	 *
	 * @param propertyIdentifier
	 * @return
	 */
}
