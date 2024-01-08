package org.taktik.icure.security.user

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.entities.EnhancedUser
import org.taktik.icure.entities.User

interface UserEnhancer {

    /**
     * Enhances a single [User] from a group database with the security data from the fallback db.
     *
     * @param user the [User] to enhance.
     * @return an [EnhancedUser].
     */
    suspend fun enhance(user: User): EnhancedUser

    /**
     * Enhances a [Flow] of [User]s from a group database with the security data from the fallback db.
     *
     * @param usersFlow a [Flow] of [User]s to enhance.
     * @return a [Flow] containing the [EnhancedUser]s, each one with the security data from the fallback db.
     */
    suspend fun enhanceFlow(usersFlow: Flow<User>): Flow<EnhancedUser>

    /**
     * Enhances a [Flow] of [User]s, each one wrapped in a [ViewQueryResultEvent], from a group database with the
     * security data from the fallback db.
     *
     * @param usersFlow a [Flow] of [ViewQueryResultEvent]s of [User] to enhance.
     * @return a [Flow] containing the [ViewQueryResultEvent]s, all the [EnhancedUser]s contained in the events are enhanced
     * with the data from the fallback db.
     */
    suspend fun enhanceViewFlow(usersFlow: Flow<ViewQueryResultEvent>): Flow<ViewQueryResultEvent>

}