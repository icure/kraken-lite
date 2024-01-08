/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.entities.CalendarItemType

interface CalendarItemTypeDAO : GenericDAO<CalendarItemType> {
	fun getCalendarItemsWithDeleted(datastoreInformation: IDatastoreInformation): Flow<CalendarItemType>
}
