package org.taktik.icure.e2e

import com.sun.net.httpserver.HttpServer
import java.io.ByteArrayOutputStream
import java.net.InetSocketAddress
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * A stand-in for the builtin views repository that `GitHubRepoDownloader` downloads.
 *
 * The downloader fetches `<repo>/archive/refs/heads/main.zip` and reads a `views.json` per root level
 * directory, so serving a synthetic zip over loopback is enough. The real
 * `https://github.com/icure/kraken-builtin-views` is deliberately not used: the suite must stay offline and
 * must not change behaviour when that repository does.
 */
class ViewsRepoStub private constructor(private val server: HttpServer) : AutoCloseable {

	val repoUrl: String get() = "http://localhost:${server.address.port}/views-repo"

	override fun close() = server.stop(0)

	companion object {

		/**
		 * @param viewsByEntity entity directory name to (view name to map function).
		 */
		fun start(viewsByEntity: Map<String, Map<String, String>>): ViewsRepoStub {
			val archive = buildArchive(viewsByEntity)
			val server = HttpServer.create(InetSocketAddress("localhost", 0), 0)

			server.createContext("/views-repo/archive/refs/heads/main.zip") { exchange ->
				exchange.responseHeaders.add("Content-Type", "application/zip")
				exchange.sendResponseHeaders(200, archive.size.toLong())
				exchange.responseBody.use { it.write(archive) }
			}
			server.start()
			return ViewsRepoStub(server)
		}

		/**
		 * Mirrors the layout of a GitHub source archive: everything sits under a single root directory, which
		 * the downloader strips with `substringAfter('/')`.
		 */
		private fun buildArchive(viewsByEntity: Map<String, Map<String, String>>): ByteArray =
			ByteArrayOutputStream().also { bytes ->
				ZipOutputStream(bytes).use { zip ->
					viewsByEntity.forEach { (entity, views) ->
						val descriptors = views.keys.associateWith { viewName ->
							mapOf(
								"map" to "$viewName.js",
								"reduce" to null,
								"weight" to 1,
								"affinities" to emptyList<String>(),
								"libResources" to emptyMap<String, String>(),
							)
						}
						zip.write("views-repo-main/$entity/views.json", objectMapper.writeValueAsString(mapOf("views" to descriptors)))
						views.forEach { (viewName, map) ->
							zip.write("views-repo-main/$entity/$viewName.js", map)
						}
					}
				}
			}.toByteArray()

		private fun ZipOutputStream.write(path: String, content: String) {
			putNextEntry(ZipEntry(path))
			write(content.toByteArray())
			closeEntry()
		}
	}
}
