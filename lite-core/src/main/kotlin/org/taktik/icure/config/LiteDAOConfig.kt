package org.taktik.icure.config

import io.icure.asyncjacksonhttpclient.net.web.WebClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.taktik.couchdb.dao.DesignDocumentProvider
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.asyncdao.CouchDbDispatcher
import org.taktik.icure.asyncdao.ICureLiteDAO
import org.taktik.icure.asyncdao.MedicalLocationDAO
import org.taktik.icure.asyncdao.MessageDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asyncdao.components.ActiveTasksProvider
import org.taktik.icure.asyncdao.impl.ICureLiteDAOImpl
import org.taktik.icure.asyncdao.impl.MedicalLocationDAOImpl
import org.taktik.icure.asyncdao.impl.MessageDAOImpl
import org.taktik.icure.asyncdao.impl.UserDAOImpl
import org.taktik.icure.cache.ConfiguredCacheProvider
import org.taktik.icure.cache.EntityCacheFactory
import org.taktik.icure.security.CouchDbCredentialsProvider

@Configuration
@Profile("app")
class LiteDAOConfig : DaoConfig {

    companion object {
        private const val USE_DATA_OWNER_PARTITION = "useDataOwnerPartition"
        private const val USE_OBSOLETE_VIEWS = "useObsoleteViews"
    }

    @Value("\${icure.dao.$USE_DATA_OWNER_PARTITION:false}")
    override var useDataOwnerPartition: Boolean = false

    @Value("\${icure.dao.$USE_OBSOLETE_VIEWS:false}")
    override var useObsoleteViews: Boolean = false

    @Value("\${icure.dao.forceForegroundIndexation:false}")
    var forceForegroundIndexation: Boolean = false

    @Value("\${icure.dao.viewsToIndexAtStartup:}")
    private var commaSeparatedViewsToIndexAtStartup: String = ""

    val viewsToIndexAtStartup: List<String>
        get() = commaSeparatedViewsToIndexAtStartup.split(",")

    @Value("\${icure.dao.backgroundIndexationWorkers:1}")
    var backgroundIndexationWorkers: Int = 1

    fun setLiteConfig(propertyName: String, value: Boolean) {
        when(propertyName) {
            USE_DATA_OWNER_PARTITION -> {
                useDataOwnerPartition = value
            }
            USE_OBSOLETE_VIEWS -> {
                useObsoleteViews = value
            }
            else -> throw IllegalArgumentException("$propertyName property is not a recognized property")
        }
    }

    @Bean
    @Profile("app")
    fun messageDAO(
        @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
        idGenerator: IDGenerator,
        entityCacheFactory: ConfiguredCacheProvider,
        designDocumentProvider: DesignDocumentProvider
    ): MessageDAO = MessageDAOImpl(
        couchDbDispatcher = couchDbDispatcher,
        idGenerator = idGenerator,
        entityCacheFactory = entityCacheFactory,
        designDocumentProvider = designDocumentProvider,
        daoConfig = this
    )

    @Bean
    @Profile("app")
    fun medicalLocationDAO(
        @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
        idGenerator: IDGenerator,
        entityCacheFactory: ConfiguredCacheProvider,
        designDocumentProvider: DesignDocumentProvider
    ): MedicalLocationDAO = MedicalLocationDAOImpl(
        couchDbDispatcher = couchDbDispatcher,
        idGenerator = idGenerator,
        entityCacheFactory = entityCacheFactory,
        designDocumentProvider = designDocumentProvider,
        daoConfig = this
    )

    @Bean
    @Profile("app")
    fun userDAO(
        @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
        idGenerator: IDGenerator,
        entityCacheFactory: ConfiguredCacheProvider,
        designDocumentProvider: DesignDocumentProvider
    ): UserDAO = UserDAOImpl(
        couchDbDispatcher = couchDbDispatcher,
        idGenerator = idGenerator,
        entityCacheFactory = entityCacheFactory,
        designDocumentProvider = designDocumentProvider,
        daoConfig = this
    )

    @Bean
    @Profile("app")
    @OptIn(ExperimentalCoroutinesApi::class)
    fun iCureDAO(
        httpClient: WebClient,
        couchDbCredentialsProvider: CouchDbCredentialsProvider,
        @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
        activeTasksProvider: ActiveTasksProvider
    ): ICureLiteDAO = ICureLiteDAOImpl(
        httpClient = httpClient,
        couchDbCredentialsProvider = couchDbCredentialsProvider,
        couchDbDispatcher = couchDbDispatcher,
        activeTasksProvider = activeTasksProvider
    )
}