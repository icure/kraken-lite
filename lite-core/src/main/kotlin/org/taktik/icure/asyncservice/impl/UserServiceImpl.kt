package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asyncservice.UserService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.User
import org.taktik.icure.entities.base.PropertyStub
import org.taktik.icure.pagination.PaginationElement

@Service
class UserServiceImpl(
    private val userLogic: UserLogic
) : UserService {
    override suspend fun createUser(user: User): User? = userLogic.createUser(user)

    override suspend fun getUser(id: String): User? = userLogic.getUser(id)

    override suspend fun getUserByEmail(email: String): User? = userLogic.getUserByEmail(email)

    override suspend fun getUserByGenericIdentifier(genericIdentifier: String): User? = userLogic.getUserByGenericIdentifier(genericIdentifier)

    override suspend fun getUserByLogin(login: String): User? = userLogic.getUserByLogin(login)

    override suspend fun getUserByPhone(phone: String): User? = userLogic.getUserByPhone(phone)

    override fun listUserIdsByHcpartyId(hcpartyId: String): Flow<String> = userLogic.listUserIdsByHcpartyId(hcpartyId)

    override fun findByPatientId(patientId: String): Flow<String> = userLogic.findByPatientId(patientId)

    override fun findByNameEmailPhone(
        searchString: String,
        pagination: PaginationOffset<String>
    ): Flow<ViewQueryResultEvent> = userLogic.findByNameEmailPhone(searchString, pagination)

    override fun getUsers(ids: List<String>): Flow<User> = userLogic.getUsers(ids)

    override fun getUsersByLogin(login: String): Flow<User> = userLogic.getUsersByLogin(login)

    override fun listUserIdsByNameEmailPhone(searchString: String): Flow<String> = userLogic.listUserIdsByNameEmailPhone(searchString)

    override fun listUsers(
        paginationOffset: PaginationOffset<String>,
        skipPatients: Boolean
    ): Flow<PaginationElement> = userLogic.listUsers(paginationOffset, skipPatients)

    override fun filterUsers(
        paginationOffset: PaginationOffset<Nothing>,
        filter: FilterChain<User>
    ): Flow<ViewQueryResultEvent> = userLogic.filterUsers(paginationOffset, filter)

    override suspend fun modifyUser(modifiedUser: User): User? = userLogic.modifyUser(modifiedUser)

    override suspend fun setProperties(userId: String, properties: List<PropertyStub>): User? = userLogic.setProperties(userId, properties)

    override suspend fun disableUser(userId: String): User? = userLogic.disableUser(userId)

    override suspend fun enableUser(userId: String): User? = userLogic.enableUser(userId)

    override suspend fun createOrUpdateToken(
        userIdentifier: String,
        key: String,
        tokenValidity: Long,
        token: String?,
        useShortToken: Boolean
    ): String = userLogic.createOrUpdateToken(userIdentifier, key, tokenValidity, token, useShortToken)

    override suspend fun deleteUser(userId: String): DocIdentifier? = userLogic.deleteUser(userId)

    override suspend fun undeleteUser(userId: String) = userLogic.undeleteUser(userId)
}
