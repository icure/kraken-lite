package org.taktik.icure.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.taktik.couchdb.dao.DesignDocumentProvider
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.asyncdao.CouchDbDispatcher
import org.taktik.icure.asyncdao.MedicalLocationDAO
import org.taktik.icure.asyncdao.MessageDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asyncdao.impl.MedicalLocationDAOImpl
import org.taktik.icure.asyncdao.impl.MessageDAOImpl
import org.taktik.icure.asyncdao.impl.UserDAOImpl
import org.taktik.icure.cache.EntityCacheFactory

@Configuration
@Profile("app")
class LiteDAOConfig {
    @Bean
    @Profile("app")
    fun messageDAO(
        @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
        idGenerator: IDGenerator,
        entityCacheFactory: EntityCacheFactory,
        designDocumentProvider: DesignDocumentProvider
    ): MessageDAO = MessageDAOImpl(couchDbDispatcher, idGenerator, entityCacheFactory, designDocumentProvider)

    @Bean
    @Profile("app")
    fun medicalLocationDAO(
        @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
        idGenerator: IDGenerator,
        entityCacheFactory: EntityCacheFactory,
        designDocumentProvider: DesignDocumentProvider
    ): MedicalLocationDAO = MedicalLocationDAOImpl(couchDbDispatcher, idGenerator, entityCacheFactory, designDocumentProvider)

    @Bean
    @Profile("app")
    fun userDAO(
        @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
        idGenerator: IDGenerator,
        entityCacheFactory: EntityCacheFactory,
        designDocumentProvider: DesignDocumentProvider
    ): UserDAO = UserDAOImpl(
        couchDbDispatcher,
        idGenerator,
        entityCacheFactory,
        designDocumentProvider
    )
}