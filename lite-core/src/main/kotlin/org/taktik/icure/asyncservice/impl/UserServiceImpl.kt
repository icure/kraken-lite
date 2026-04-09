package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asyncservice.UserService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.User
import org.taktik.icure.entities.base.PropertyStub
import org.taktik.icure.entities.conflicts.ConflictResolutionResult
import org.taktik.icure.entities.conflicts.MergeResult
import org.taktik.icure.pagination.PaginationElement

@Service
class UserServiceImpl(
	private val userLogic: UserLogic
) : UserService {
	override suspend fun createUser(user: User): User = userLogic.createUser(user)
	override fun createUsers(users: List<User>): Flow<User> = userLogic.createEntities(users)

	override suspend fun getUser(id: String, includeMetadataFromGlobalUser: Boolean): User? = userLogic.getUser(id, false)

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

	override fun listUsers(
		paginationOffset: PaginationOffset<String>,
		skipPatients: Boolean
	): Flow<PaginationElement> = userLogic.listUsers(paginationOffset, skipPatients)

	override fun filterUsers(
		paginationOffset: PaginationOffset<Nothing>,
		filter: FilterChain<User>
	): Flow<ViewQueryResultEvent> = userLogic.filterUsers(paginationOffset, filter)

	override fun matchUsersBy(filter: AbstractFilter<User>): Flow<String> = userLogic.matchEntitiesBy(filter)

	override suspend fun modifyUser(modifiedUser: User): User = userLogic.modifyUser(modifiedUser)
	override fun modifyUsers(users: List<User>): Flow<User> = userLogic.modifyEntities(users)

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

	override suspend fun changeUserEmail(
		userId: String,
		newEmail: String,
		previousEmail: String?
	): User = userLogic.changeUserEmail(userId, newEmail, previousEmail)

	override suspend fun changeUserMobilePhone(
		userId: String,
		newMobilePhone: String,
		previousMobilePhone: String?
	): User = userLogic.changeUserMobilePhone(userId, newMobilePhone, previousMobilePhone)

	override suspend fun changeUserPassword(
		userId: String,
		newPassword: String
	): User = userLogic.changeUserPassword(userId, newPassword)

	override suspend fun deleteUser(id: String, rev: String?): User = userLogic.deleteEntity(id, rev)
	override fun deleteUsers(userIds: List<IdAndRev>): Flow<DocIdentifier> = userLogic.deleteEntities(userIds).map { DocIdentifier(it.id, it.rev) }

	override suspend fun purgeUser(id: String, rev: String): DocIdentifier = userLogic.purgeEntity(id, rev)
	override fun purgeUsers(userIds: List<IdAndRev>): Flow<DocIdentifier> = userLogic.purgeEntities(userIds)

	override suspend fun undeleteUser(id: String, rev: String): User = userLogic.undeleteEntity(id, rev)
	override fun undeleteUsers(userIds: List<IdAndRev>): Flow<User> = userLogic.undeleteEntities(userIds)

	override fun getConflictingEntitiesIds(): Flow<String> = userLogic.getConflictingEntitiesIds()
	override fun getConflictsFor(entityId: String): Flow<User> = userLogic.getConflictsFor(entityId)
	override suspend fun declareConflictWinner(
		entity: User,
		conflictsToPurge: List<String>
	): ConflictResolutionResult<User> {
		val conflicts = conflictsToPurge.mapNotNull { rev ->
			userLogic.getBypassingCache(entity.id, rev)
		}
		return userLogic.declareConflictWinner(entity, conflicts)
	}
	override fun solveConflicts(
		limit: Int?,
		ids: List<String>?
	): Flow<MergeResult> = userLogic.solveConflicts(limit, ids)
}
