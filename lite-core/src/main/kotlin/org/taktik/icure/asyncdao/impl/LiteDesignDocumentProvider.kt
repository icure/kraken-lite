package org.taktik.icure.asyncdao.impl

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.taktik.couchdb.Client
import org.taktik.couchdb.dao.DesignDocumentProvider
import org.taktik.couchdb.dao.designDocName
import org.taktik.couchdb.entity.DesignDocument
import org.taktik.couchdb.entity.Indexer
import org.taktik.couchdb.entity.View
import org.taktik.couchdb.entity.ViewQuery
import org.taktik.couchdb.queryView
import org.taktik.couchdb.support.StdDesignDocumentFactory
import org.taktik.icure.asyncdao.Partitions
import org.taktik.icure.properties.CouchDbLiteProperties
import java.time.Duration
import java.util.concurrent.TimeUnit

@Service
@OptIn(DelicateCoroutinesApi::class)
class LiteDesignDocumentProvider(
    private val couchDbProperties: CouchDbLiteProperties
) : DesignDocumentProvider {
    private data class DesignDocInfo(val metaDataSource: Any, val entityClass: Class<*>, val partition: String?)

    private suspend fun deleteStaleDesignDocuments(client: Client, relatedDesignDocuments: List<String>) {
        client.bulkDelete(
            relatedDesignDocuments.mapNotNull { client.get(it, DesignDocument::class.java) }
        ).collect()
    }

    private val designDocIdProvider = CacheBuilder.newBuilder()
        .maximumSize(100)
        .expireAfterAccess(couchDbProperties.cachedDesignDocumentTtlMinutes, TimeUnit.MINUTES)
        .build(object : CacheLoader<Pair<Client, DesignDocInfo>, Deferred<String>>() {
            @Throws(Exception::class)
            override fun load(key: Pair<Client, DesignDocInfo>): Deferred<String> {
                return GlobalScope.async {
                    val client = key.first
                    val designDocInfo = key.second
                    val baseId = baseDesignDocumentId(designDocInfo.entityClass, designDocInfo.partition)
                    val relatedDesignDocs = client.designDocumentsIds().filter { if (it.length == baseId.length) it.startsWith(baseId) else it.startsWith("${baseId}_") }
                    val generatedDesignDocumentId = currentDesignDocumentId(designDocInfo.entityClass, designDocInfo.metaDataSource, designDocInfo.partition)

                    return@async if (relatedDesignDocs.size == 1) {
                        relatedDesignDocs.first()
                    } else if (relatedDesignDocs.contains(generatedDesignDocumentId) && isReadyDesignDoc(client, generatedDesignDocumentId)) {
                        deleteStaleDesignDocuments(client, relatedDesignDocs.filter { it != generatedDesignDocumentId })
                        generatedDesignDocumentId
                    } else {
                        relatedDesignDocs.filter { it != generatedDesignDocumentId }.firstOrNull { isReadyDesignDoc(client, it) }
                            ?: throw IllegalStateException("No design doc for $baseId can be found at this time")
                    }
                }
            }
        })
    private val viewsBeingIndexed = CacheBuilder.newBuilder()
        .maximumSize(1)
        .expireAfterAccess(10, TimeUnit.SECONDS)
        .build(object : CacheLoader<Client, Deferred<List<String>>>() {
            override fun load(client: Client): Deferred<List<String>> = GlobalScope.async {
                return@async client.activeTasks()
                    .filterIsInstance<Indexer>()
                    .mapNotNull { it.design_document }
            }
        })

    private suspend fun isReadyDesignDoc(client: Client, designDocumentId: String): Boolean =
        viewsBeingIndexed.get(client).await().takeIf { it.contains(designDocumentId) }?.let { false }
            ?: client.queryView<String, String>(ViewQuery().designDocId(designDocumentId).viewName("all").limit(1), Duration.ofMillis(couchDbProperties.designDocumentStatusCheckTimeoutMilliseconds))
                .map { true }
                .catch { emit(false) }
                .firstOrNull() ?: true


    override fun baseDesignDocumentId(entityClass: Class<*>, secondaryPartition: String?): String =
        designDocName(entityClass.simpleName, secondaryPartition)

    override suspend fun currentOrAvailableDesignDocumentId(client: Client, entityClass: Class<*>, metaDataSource: Any, secondaryPartition: String?): String {
        return designDocIdProvider.get(client to DesignDocInfo(metaDataSource, entityClass, secondaryPartition)).await()
    }

    override suspend fun currentDesignDocumentId(entityClass: Class<*>, metaDataSource: Any, secondaryPartition: String?): String =
        baseDesignDocumentId(entityClass, secondaryPartition).let { baseId ->
            generateDesignDocuments(entityClass, metaDataSource).find { it.id.startsWith(baseId) }
                ?: throw IllegalStateException("No design doc for $baseId can be found at this time")
        }.id

    private fun View.normalizedMap() = map
        .replace("map\\s*=\\s*function\\s*".toRegex(), "function")
        .replace("\r?\n".toRegex(), "")
        .replace("\\s+".toRegex(), " ")
        .replace(" == ", " === ")
        .replace("[ ;]$".toRegex(), "")

    private infix fun DesignDocument.equipollent(other: DesignDocument): Boolean =
        views.entries.all { (viewName, view) ->
            other.views[viewName]?.let {
                it.reduce == view.reduce && it.normalizedMap() == view.normalizedMap()
            } ?: false
        }

    override suspend fun generateDesignDocuments(entityClass: Class<*>, metaDataSource: Any, client: Client?, partition: Partitions): Set<DesignDocument> {
        val existingIds = client?.designDocumentsIds() ?: emptySet()
        return StdDesignDocumentFactory().generateFrom(baseDesignDocumentId(entityClass), metaDataSource, useVersioning = true).filter { dd ->
            when(partition) {
                Partitions.All -> true
                Partitions.Main -> "^_design/${entityClass.simpleName}(_[a-z0-9]+)?".toRegex().matches(dd.id)
                else -> "^_design/${entityClass.simpleName}-${partition.partitionName}(_[a-z0-9]+)?".toRegex().matches(dd.id)
            }
        }.mapNotNull { dd ->
            val (name, _) = dd.id.lastIndexOf('_').let { dd.id.substring(0, it) to dd.id.substring(it + 1) }
            val currentDocument = existingIds.firstOrNull { it.substring(0, it.lastIndexOf('_')) == name }?.let { id ->
                client?.get(id, DesignDocument::class.java)
            }
            dd.takeIf { currentDocument == null || !(dd equipollent currentDocument) }
        }.toSet()
    }

}
