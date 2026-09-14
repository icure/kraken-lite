package org.taktik.icure.e2e

import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.taktik.icure.asyncdao.impl.GenericDAOImpl
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.Modifier

/**
 * The DAOs kraken-lite registers, discovered the same way Spring does: every concrete, stereotype annotated
 * DAO implementation under `org.taktik.icure.asyncdao`, which is one of the packages listed in
 * `scanBasePackages` of [org.taktik.icure.ICureBackendApplication].
 *
 * Discovering them rather than hardcoding a list is the point: a DAO added later is picked up automatically,
 * so the "every DAO gets a design document" check cannot silently stop covering new entities.
 */
object DaoCatalog {

	/**
	 * Entity simple names, one per registered DAO. These are what the design document ids are built from:
	 * `DesignDocumentProvider.baseDesignDocumentId` uses `entityClass.simpleName`.
	 */
	val entityNames: List<String> by lazy {
		val reflections = Reflections("org.taktik.icure.asyncdao", SubTypesScanner(false))

		(reflections.getSubTypesOf(GenericDAOImpl::class.java) + reflections.getSubTypesOf(InternalDAOImpl::class.java))
			.asSequence()
			.filter { !Modifier.isAbstract(it.modifiers) && !it.isInterface }
			// Deliberately not filtered on a Spring stereotype: LiteDAOConfig registers UserDAOImpl,
			// MessageDAOImpl and MedicalLocationDAOImpl as @Bean rather than annotating them, so an
			// annotation based filter would silently skip exactly those three. Classes with no entity type
			// argument, such as the GenericIcureDAOImpl base, drop out through entityNameOf returning null.
			.mapNotNull { entityNameOf(it) }
			.distinct()
			.sorted()
			.toList()
	}

	/**
	 * Recovers the entity a DAO is parameterised with, e.g. `AccessLog` for
	 * `AccessLogDAOImpl : ConflictDAOImpl<AccessLog>`. The entity is never available as a constant on the
	 * class, but it is baked into the generic supertype, so the type argument can be read back.
	 */
	private fun entityNameOf(dao: Class<*>): String? {
		var current: Class<*>? = dao
		while (current != null && current != Any::class.java) {
			entityArgumentOf(current.genericSuperclass)?.let { return it.simpleName }
			current = current.superclass
		}
		return null
	}

	private fun entityArgumentOf(type: Type?): Class<*>? = (type as? ParameterizedType)
		?.actualTypeArguments
		?.filterIsInstance<Class<*>>()
		?.firstOrNull { it.name.startsWith("org.taktik.icure.entities.") }
}
