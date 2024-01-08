/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import org.taktik.couchdb.entity.Option
import org.taktik.couchdb.id.Identifiable
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation

interface LookupDAO<T : Identifiable<String>> {
	/**
	 * Get an existing entity
	 *
	 * @param datastoreInformation an IDataStoreInformation instance
	 * @param id Id of the entity to get
	 * @param options Any eventual option for fetching the entity. Used if you need to retrieve conflicting revisions,
	 * revisions' history, etc...
	 * @return The entity
	 */
	suspend fun get(datastoreInformation: IDatastoreInformation, id: String, vararg options: Option): T?

	/**
	 * Gets a specific revision of an existing entity
	 *
	 * @param datastoreInformation an IDataStoreInformation instance
	 * @param id Id of the entity to get
	 * @param options Any eventual option for fetching the entity. Used if you need to retrieve conflicting revisions,
	 * revisions' history, etc...
	 * @return The entity
	 */
	suspend fun get(datastoreInformation: IDatastoreInformation, id: String, rev: String?, vararg options: Option): T?

	/**
	 * Save entity and indicate it is a new entity
	 *
	 * @param datastoreInformation an IDataStoreInformation instance
	 * @param entity The entity to save
	 * @return Returns the saved entity
	 */
	suspend fun create(datastoreInformation: IDatastoreInformation, entity: T): T?

	/**
	 * Save entity without knowing if it's a new entity or not
	 *
	 * @param datastoreInformation an IDataStoreInformation instance
	 * @param entity The entity to save
	 * @return Returns the saved entity
	 */
	suspend fun save(datastoreInformation: IDatastoreInformation, entity: T): T?
}
