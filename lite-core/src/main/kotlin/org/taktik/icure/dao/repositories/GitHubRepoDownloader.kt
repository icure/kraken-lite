package org.taktik.icure.dao.repositories

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.zip.ZipInputStream
import kotlin.collections.mapValues

@Component
class GitHubRepoDownloader(
	private val objectMapper: ObjectMapper,
) {

	/**
	 * Downloads the views repository archive in memory (without persisting it on disk) and, for each
	 * root-level directory, deserializes its `views.json` (if present) as a [Map] of view name to [ViewDescriptor].
	 *
	 * @return a map from each root-level directory name to the descriptors declared in its `views.json`.
	 */
	suspend fun downloadViewsFromRepo(
		viewsRepoUrl: String,
	): Map<String, Map<String, ViewDescriptor>> = withContext(Dispatchers.IO) {
		val archiveUrl = buildString {
			append(viewsRepoUrl.removeSuffix(".git"))
			append("/archive/refs/heads/main.zip")
		}

		val httpClient = HttpClient.newBuilder()
			.followRedirects(HttpClient.Redirect.NORMAL)
			.build()

		val request = HttpRequest.newBuilder()
			.uri(URI.create(archiveUrl))
			.GET()
			.build()

		val response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream())
		check(response.statusCode() in 200..299) {
			"Failed to download view repo archive: HTTP ${response.statusCode()} from $archiveUrl"
		}

		val files = mutableMapOf<String, ByteArray>()
		ZipInputStream(response.body()).use { zip ->
			var entry = zip.nextEntry
			while (entry != null) {
				if (!entry.isDirectory) {
					files[entry.name.substringAfter('/')] = zip.readBytes()
				}
				zip.closeEntry()
				entry = zip.nextEntry
			}
		}

		files.asSequence()
			.filter { (path, _) -> path.count { it == '/' } == 1 && path.substringAfterLast('/') == VIEWS_FILE_NAME }
			.associate { (path, bytes) ->
				val entity = path.substringBefore('/')
				val viewsNode = objectMapper.readTree(bytes).get(VIEWS_NODE_NAME)
				val views = if (viewsNode == null) {
					emptyMap()
				} else {
					objectMapper.convertValue<Map<String, ViewDescriptor>>(viewsNode).mapValues { (_, descriptor) ->
						descriptor.copy(
							map = resourceText(files, entity, descriptor.map),
							reduce = descriptor.reduce?.let { reduce ->
								if (reduce.startsWith("_")) reduce else resourceText(files, entity, reduce)
							},
							libResources = descriptor.libResources.mapValues { (_, libFile) ->
								libContent(files, libFile)
							}
						)
					}
				}
				entity to views
			}
	}

	/**
	 * Reads the UTF-8 text of a resource from the buffered archive. Map and reduce files are resolved relative to the
	 * entity directory; lib resources are resolved against the top-level `lib` directory.
	 */
	private fun resourceText(files: Map<String, ByteArray>, entity: String, path: String): String {
		val bytes = requireNotNull(files["$entity/$path"]) {
			"Referenced resource not found in views repository archive: $entity/$path"
		}
		return bytes.toString(Charsets.UTF_8)
	}

	private fun libContent(files: Map<String, ByteArray>, lib: String): String {
		val bytes = requireNotNull(files["lib/$lib"]) {
			"Referenced resource not found in views repository archive: lib/$lib"
		}
		return bytes.toString(Charsets.UTF_8)
	}

	data class ViewDescriptor(
		val map: String,
		val reduce: String?,
		val weight: Long?,
		val affinities: List<String>,
		val libResources: Map<String, String>
	)

	private companion object {
		const val VIEWS_FILE_NAME = "views.json"
		const val VIEWS_NODE_NAME = "views"
	}

}
