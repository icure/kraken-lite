package org.taktik.icure.security.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import org.springframework.stereotype.Component
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.icure.entities.EnhancedUser
import org.taktik.icure.entities.User

@Component
class NoUserEnhancer : UserEnhancer {
    override suspend fun enhance(user: User, includeMetadataFromGlobalUser: Boolean) = user.enhanceWith(null)

    override suspend fun enhanceFlow(usersFlow: Flow<User>, includeMetadataFromGlobalUser: Boolean) = usersFlow.map {
        it.enhanceWith(null)
    }

    override suspend fun enhanceViewFlow(usersFlow: Flow<ViewQueryResultEvent>, includeMetadataFromGlobalUser: Boolean) = usersFlow.transform {
        if (it is ViewRowWithDoc<*, *, *>) {
            @Suppress("UNCHECKED_CAST")
            emit((it as ViewRowWithDoc<*, *, User>).copy(doc = it.doc.enhanceWith(null)))
        } else {
            emit(it)
        }
    }
}
