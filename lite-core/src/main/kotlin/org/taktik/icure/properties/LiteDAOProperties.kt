package org.taktik.icure.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("icure.dao")
class LiteDAOProperties(
	/**
	 * Initial value of the data owner partition flag. It can be changed at runtime through
	 * [org.taktik.icure.config.LiteDAOConfig.setLiteConfig].
	 */
	val useDataOwnerPartition: Boolean = false,
	/**
	 * Initial value of the obsolete views flag. It can be changed at runtime through
	 * [org.taktik.icure.config.LiteDAOConfig.setLiteConfig].
	 */
	val useObsoleteViews: Boolean = false,
	/**
	 * If true, all the views are indexed in foreground at startup.
	 */
	val forceForegroundIndexation: Boolean = false,
	/**
	 * Views (`<EntityName>` or `<EntityName>_<partition>`) to index in foreground at startup.
	 */
	val viewsToIndexAtStartup: List<String> = emptyList(),
	/**
	 * Number of workers used for the background indexation.
	 */
	val backgroundIndexationWorkers: Int = 1,
	/**
	 * If true, the built-in views are indexed at startup.
	 */
	val indexBuiltInViews: Boolean = true,
)
