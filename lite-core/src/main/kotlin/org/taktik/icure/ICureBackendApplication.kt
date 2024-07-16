/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Component
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asyncdao.ICureDAO
import org.taktik.icure.asyncdao.InternalDAO
import org.taktik.icure.asyncdao.Partitions
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.asynclogic.ICureLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.datastore.DatastoreInstanceProvider
import org.taktik.icure.asynclogic.datastore.IDatastoreInformation
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorage
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorageMigration
import org.taktik.icure.config.ExternalViewsConfig
import org.taktik.icure.constants.Users
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.User
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.entities.embed.Confidentiality
import org.taktik.icure.entities.embed.DocumentStatus
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.InsuranceStatus
import org.taktik.icure.entities.embed.PartnershipStatus
import org.taktik.icure.entities.embed.PartnershipType
import org.taktik.icure.entities.embed.PaymentType
import org.taktik.icure.entities.embed.PersonalStatus
import org.taktik.icure.entities.embed.TelecomType
import org.taktik.icure.entities.embed.Visibility
import org.taktik.icure.properties.AuthenticationLiteProperties
import org.taktik.icure.properties.AuthenticationProperties
import org.taktik.icure.properties.CouchDbLiteProperties
import org.taktik.icure.utils.suspendRetry
import java.util.*
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@SpringBootApplication(
    scanBasePackages = [
        "org.springframework.boot.autoconfigure.aop",
        "org.springframework.boot.autoconfigure.context",
        "org.springframework.boot.autoconfigure.validation",
        "org.springframework.boot.autoconfigure.websocket",
        "org.taktik.icure.application",
        "org.taktik.icure.config",
        "org.taktik.icure.cache",
        "org.taktik.icure.security",
        "org.taktik.icure.asyncdao",
        "org.taktik.icure.asynclogic",
        "org.taktik.icure.asyncservice",
        "org.taktik.icure.be.ehealth.logic",
        "org.taktik.icure.properties",
        "org.taktik.icure.services.external.http",
        "org.taktik.icure.services.external.rest",
        "org.taktik.icure.scheduledtask",
        "org.taktik.icure.errors",
        "org.taktik.icure.be.format.logic",
        "org.taktik.icure.db",
        "org.taktik.icure.services.external.rest.v1.controllers",
        "org.taktik.icure.services.external.rest.v1.controllers.support",
        "org.taktik.icure.services.external.rest.v1.mapper",
        "org.taktik.icure.services.external.rest.v2.mapper"
    ],
    exclude = [
        FreeMarkerAutoConfiguration::class,
        CacheAutoConfiguration::class,
        DataSourceAutoConfiguration::class,
        JndiDataSourceAutoConfiguration::class,
        ErrorWebFluxAutoConfiguration::class
    ]
)
@EnableScheduling
class ICureBackendApplication {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Bean
    @Profile("app")
    fun performStartupTasks(
        @Qualifier("threadPoolTaskExecutor") taskExecutor: TaskExecutor,
        taskScheduler: TaskScheduler,
        userLogic: UserLogic,
        iCureLogic: ICureLogic,
        codeLogic: CodeLogic,
        iCureDAO: ICureDAO,
        allDaos: List<GenericDAO<*>>,
        allInternalDaos: List<InternalDAO<*>>,
        couchDbProperties: CouchDbLiteProperties,
        authenticationProperties: AuthenticationProperties,
        authenticationLiteProperties: AuthenticationLiteProperties,
        allObjectStorageLogic: List<IcureObjectStorage<*>>,
        allObjectStorageMigrationLogic: List<IcureObjectStorageMigration<*>>,
        datastoreInstanceProvider: DatastoreInstanceProvider,
        externalViewsConfig: ExternalViewsConfig
    ) = ApplicationRunner {
        //Check that core types have corresponding codes
        log.info("icure (" + iCureLogic.getVersion() + ") is initialised")

        runBlocking {
            allDaos.forEach { dao ->
                dao.forceInitStandardDesignDocument(datastoreInstanceProvider.getInstanceAndGroup(), true, partition = Partitions.Main, ignoreIfUnchanged = true)
            }
            allInternalDaos.forEach { dao ->
                dao.forceInitStandardDesignDocument(true)
            }
            deferDataOwnerDesignDocIndexation(allDaos, iCureDAO, externalViewsConfig.repos, datastoreInstanceProvider.getInstanceAndGroup())
            allObjectStorageLogic.forEach { logic -> logic.rescheduleFailedStorageTasks() }
            allObjectStorageMigrationLogic.forEach { logic -> logic.rescheduleStoredMigrationTasks() }

            if (authenticationLiteProperties.createAdminUser && suspendRetry(10) { userLogic.listUsers(PaginationOffset(1), true).filterIsInstance<ViewRowWithDoc<String, Nothing, User>>().toList().isEmpty() } ) {
                val password = UUID.randomUUID().toString().substring(0,13).replace("-","")
                userLogic.createUser(User(id = UUID.randomUUID().toString(), login = "admin", passwordHash = password, type =  Users.Type.database, status = Users.Status.ACTIVE))

                log.warn("Default admin user created with password $password")
            }
        }

        taskExecutor.execute {
            listOf(
                AddressType::class.java,
                DocumentType::class.java,
                DocumentStatus::class.java,
                Gender::class.java,
                InsuranceStatus::class.java,
                PartnershipStatus::class.java,
                PartnershipType::class.java,
                PaymentType::class.java,
                PersonalStatus::class.java,
                TelecomType::class.java,
                Confidentiality::class.java,
                Visibility::class.java
            ).forEach { runBlocking { codeLogic.importCodesFromEnum(it) } }
        }

        if (couchDbProperties.populateDatabaseFromLocalXmls) {
            taskExecutor.execute {
                log.info("Importing codes from local xmls")
                val resolver = PathMatchingResourcePatternResolver(javaClass.classLoader)
                resolver.getResources("classpath*:/org/taktik/icure/db/codes/**.xml").forEach {
                    val md5 = it.filename!!.replace(Regex(".+\\.([0-9a-f]{20}[0-9a-f]+)\\.xml"), "$1")
                    runBlocking { codeLogic.importCodesFromXml(md5, it.filename!!.replace(Regex("(.+)\\.[0-9a-f]{20}[0-9a-f]+\\.xml"), "$1"), it.inputStream) }
                }
                log.info("Import completed")
            }
        }

        log.info("icure (" + iCureLogic.getVersion() + ") is started")
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun deferDataOwnerDesignDocIndexation(
        genericDAOs: List<GenericDAO<*>>,
        iCureDAO: ICureDAO,
        externalViewRepositories: Map<String, String>,
        datastoreInformation: IDatastoreInformation
    ) = GlobalScope.launch {

        suspend fun isIndexingWithDebouncing(): Boolean {
            val firstAttempt = iCureDAO.getIndexingStatus(datastoreInformation).isNotEmpty()
            delay(1L.seconds.inWholeMilliseconds)
            val secondAttempt = iCureDAO.getIndexingStatus(datastoreInformation).isNotEmpty()
            delay(1L.seconds.inWholeMilliseconds)
            val thirdAttempt = iCureDAO.getIndexingStatus(datastoreInformation).isNotEmpty()
            return  firstAttempt || secondAttempt || thirdAttempt
        }

        suspend fun warmupPartitionAndCheckForCompletion(
            dao: GenericDAO<*>,
            datastoreInformation: IDatastoreInformation,
            partition: Partitions
        ): Boolean = runCatching {
            dao.warmupPartition(datastoreInformation, partition)
            true
        }.getOrDefault(false)

        // It is important to index All Maurice partition before the DataOwner ones
        listOf(Partitions.Maurice, Partitions.DataOwner).forEach { partition ->
            log.info("Deferring indexation of $partition design docs.")
            genericDAOs.forEach {
                while(isIndexingWithDebouncing()) {
                    delay(1L.minutes.inWholeMilliseconds)
                }
                log.info("Indexing design docs for ${it::class.java.simpleName}")
                it.forceInitStandardDesignDocument(datastoreInformation, true, partition = partition, ignoreIfUnchanged = true)
                while(!warmupPartitionAndCheckForCompletion(it, datastoreInformation, partition)) {
                    delay(1L.seconds.inWholeMilliseconds)
                }
            }
            log.info("Indexation of $partition design docs completed.")
        }

        log.info("Starting indexation of external views")
        genericDAOs.forEach {
            while(isIndexingWithDebouncing()) {
                delay(1L.minutes.inWholeMilliseconds)
            }
            log.info("Indexing external design docs for ${it::class.java.simpleName}")
            it.forceInitExternalDesignDocument(datastoreInformation, externalViewRepositories, updateIfExists = true, dryRun = false, ignoreIfUnchanged = true)
        }

        log.info("Indexation of external design docs completed")
    }

    @Component
    @Profile("cmd")
    class Commander(val applicationContext: ConfigurableApplicationContext) : CommandLineRunner {
        private val log = LoggerFactory.getLogger(this.javaClass)

        @ExperimentalCoroutinesApi
        override fun run(vararg args: String) {
            if (args.firstOrNull() != "cmd") {
                throw IllegalStateException("first argument should be profile cmd")
            }
            val tailArgs = args.drop(1)
            log.info("icure commander started. Executing ${tailArgs.firstOrNull()}")

            // TODO: Add support for plugins from build time added jars
            // when (tailArgs.firstOrNull()) {
            //
            // }
            applicationContext.close()

            runBlocking {
                //Give time for the application to close
                delay(30000)
                exitProcess(0)
            }
        }
    }
}

fun main(args: Array<String>) {
    val profile = args.firstOrNull() ?: "app"
    SpringApplicationBuilder(ICureBackendApplication::class.java)
        .profiles(profile)
        .web(if (profile == "app") WebApplicationType.REACTIVE else WebApplicationType.NONE)
        .run(*args)
}
