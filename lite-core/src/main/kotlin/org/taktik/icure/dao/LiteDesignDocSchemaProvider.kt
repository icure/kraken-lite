package org.taktik.icure.dao

import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.create
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.entity.DesignDocument
import org.taktik.couchdb.entity.View
import org.taktik.couchdb.entity.ViewQuery
import org.taktik.couchdb.get
import org.taktik.couchdb.queryView
import org.taktik.icure.asyncdao.CouchDbDAO
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.config.LiteDAOConfig
import org.taktik.icure.dao.repositories.GitHubRepoDownloader
import org.taktik.icure.datastore.IDatastoreInformation
import org.taktik.icure.entities.designdoc.DesignDocSchema
import org.taktik.icure.properties.DesignDocSchemaProperties
import kotlin.time.Duration.Companion.seconds

@Service
class LiteDesignDocSchemaProvider(
	private val designDocProperties: DesignDocSchemaProperties,
	private val gitHubRepoDownloader: GitHubRepoDownloader
) : DesignDocSchemaProvider {

	private val logger = LoggerFactory.getLogger(this.javaClass)
	private var localDesignDocSchema: DesignDocSchema? = null

	override suspend fun getOrRequestSchema(datastore: IDatastoreInformation): DesignDocSchema? =
		localDesignDocSchema

	suspend fun <T> initializeViewsAndCreateLocalSchema(
		daoList: List<T>,
		datastoreInformation: IDatastoreInformation,
		daoConfig: LiteDAOConfig,
		isIndexing: suspend () -> Boolean,
	) where T : GenericDAO<*>, T : CouchDbDAO {
		if (designDocProperties.builtinViewsRepository != null) {
			logger.info("Starting generation of schema design docs")
			val daoByEntity = daoList.associateBy { it.entityClass.simpleName }
			val loadedViews = gitHubRepoDownloader.downloadViewsFromRepo(designDocProperties.builtinViewsRepository!!)
			val viewToPartitionByEntity = designDocProperties.viewsByEntity.map { (entity, views) ->
				logger.info("Creating design docs for $entity")
				val dao = daoByEntity[entity] ?: throw IllegalStateException("Invalid config, unknown entity class $entity")
				val client = dao.couchDbDispatcher.getClient(datastoreInformation)
				// The new design doc system creates a single view per partition, to optimize access time per view
				val configDesignDocsByView = client.designDocumentsIds().filter {
					it.matches("^_design/$entity-[0-9]+$".toRegex())
				}.mapNotNull { ddocId ->
					client.get<DesignDocument>(ddocId)
				}.associateBy {
					it.views.keys.single()
				}
				var currentMaxPartition = configDesignDocsByView.values.maxOfOrNull {
					it.getPartitionIndex()
				} ?: 0
				entity to views.associate { viewName ->
					if (configDesignDocsByView.containsKey(viewName)) {
						viewName to configDesignDocsByView.getValue(viewName).getPartitionIndex()
					} else {
						val loadedView = loadedViews[entity.lowercase()]?.get(viewName)
							?: throw IllegalStateException("Invalid config, unknown view $viewName for entity $entity")
						viewName to DesignDocument(
							id = "_design/$entity-${++currentMaxPartition}",
							views = mapOf(
								viewName to View(
									map = loadedView.map,
									reduce = loadedView.reduce,
								)
							),
							lib = loadedView.libResources,
						).let {
							client.create(it).getPartitionIndex()
						}
					}
				}
			}.toMap()
			val schema = DesignDocSchema(
				rev = null,
				applicationGroupId = "LOCAL",
				version = 0,
				committed = true,
				viewsByEntity = viewToPartitionByEntity
			)
			localDesignDocSchema = schema
			maybeWarmUpDocs(
				daoList = daoList,
				datastoreInformation = datastoreInformation,
				daoConfig = daoConfig,
				schema = schema,
				isIndexing = isIndexing,
			)
			logger.info("Generation of schema design docs completed")
		}
	}

	private suspend fun <T> maybeWarmUpDocs(
		daoList: List<T>,
		datastoreInformation: IDatastoreInformation,
		daoConfig: LiteDAOConfig,
		schema: DesignDocSchema,
		isIndexing: suspend () -> Boolean,
	) where T : GenericDAO<*>, T : CouchDbDAO {
		logger.info("Starting warmup of schema design docs")
		daoList.filter { dao ->
			daoConfig.forceForegroundIndexation
				|| daoConfig.viewsToIndexAtStartup.contains(dao.entityClass.simpleName)
		}.forEach { dao ->
			val client = dao.couchDbDispatcher.getClient(datastoreInformation)

			suspend fun warmup(
				docId: String,
				viewName: String,
			): Boolean = runCatching {
				val query = ViewQuery()
					.designDocId(docId)
					.viewName(viewName)
					.includeDocs(false)
					.limit(1)
				client.queryView<ComplexKey, JsonNode>(query).firstOrNull()
				true
			}.getOrDefault(false)

			schema.viewsByEntity[dao.entityClass.simpleName]?.forEach { (viewName, partitionIndex) ->
				while(isIndexing()) {
					delay(30L.seconds)
				}
				logger.info("Warming up $viewName-$partitionIndex for entity ${dao.entityClass.simpleName}")
				while(!warmup(docId = "_design/${dao.entityClass.simpleName}-$partitionIndex", viewName)) {
					delay(1L.seconds)
				}
			}
		}
	}

	private fun DesignDocument.getPartitionIndex() =
		id.removePrefix("_design/").split("-").last().toInt()
}