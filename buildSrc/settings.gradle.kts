dependencyResolutionManagement {
	@Suppress("UnstableApiUsage")
	repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
	@Suppress("UnstableApiUsage")
	repositories {
		mavenCentral()
		mavenLocal()
		maven { url = uri("https://maven.taktik.be/content/groups/public") }
	}

	versionCatalogs {
		create("coreLibs") {
			from(files("../kraken-common/libs.versions.toml"))
		}
	}
}
