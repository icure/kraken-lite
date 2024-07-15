package org.taktik.icure.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI
import java.net.URLClassLoader
import java.io.File
import java.util.jar.JarFile

@Component
class PluginsManager(
	@Value("\${icure.lite.plugins.sourceFolder:#{null}}") private val sourceFolder: String? = null
) {
	private val log = LoggerFactory.getLogger(this.javaClass)

	class InvalidPluginException(className: String) : IllegalArgumentException("Invalid plugin $className")

	private val loadedClasses = sourceFolder?.let { folderPath ->
		val pluginsFolder = File(folderPath)
		log.info("Loading kraken plugins from ${pluginsFolder.absolutePath}")
		if (pluginsFolder.exists()) {
			val jarFiles = pluginsFolder
				.walk()
				.filter { it.isFile && it.name.endsWith(".jar") }
				.associate { pluginFile ->
					log.info("Loading ${pluginFile.name} plugin")
					log.info("URI: jar:${pluginFile.toURI()}!/)}")
					URI.create("jar:${pluginFile.toURI()}!/") to JarFile(pluginFile)
				}

			val parentClassLoader = this::class.java.classLoader
			val classLoader = URLClassLoader.newInstance(jarFiles.keys.map { it.toURL() }.toTypedArray(), parentClassLoader)
			jarFiles.values.flatMap { jarFile ->
				jarFile.entries().asSequence().filter {
					it.name.matches("^(.+/)+[^$]+\\.class$".toRegex())
				}.map { entry ->
					entry.name.replace(".class", "").replace("/", ".")
				}.toList()
			}.associateWith { className -> classLoader.loadClass(className) }
		} else null
	} ?: emptyMap()


	fun instantiate(className: String, vararg args: Any?) =
		loadedClasses[className]?.getDeclaredConstructor(*args.map { it?.javaClass ?: Nothing::class.java }.toTypedArray())?.newInstance(*args)
			?: throw InvalidPluginException(className)
}

inline fun <reified T : Any> PluginsManager.newInstance(className: String, vararg args: Any?): T =
	instantiate(className, *args) as T
