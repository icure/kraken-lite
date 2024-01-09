pluginManagement {
    includeBuild("./kraken-common/build-logic")

    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.taktik.be/content/groups/public") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
        maven { url = uri("https://repo.spring.io/plugins-release") }
        maven { url = uri("https://jitpack.io") }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "kraken-lite"

val sharedDeps = listOf(
    "utils",
    "domain",
    "dto",
    "dao",
    "logic",
    "jwt",
    "mapper",
    "core",
    "service"
)

val liteDeps = listOf(
    "lite-core",
)

include(
    sharedDeps.map { ":kraken-common:$it" } + liteDeps.map { ":$it" }
)

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://maven.taktik.be/content/groups/public") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://repo.spring.io/plugins-release") }
    }

    versionCatalogs {
        create("coreLibs") {
            from(files("./kraken-common/libs.versions.toml"))
        }
        create("liteLibs") {
            from(files("./libs.versions.toml"))
        }
    }
}

//sharedDeps.onEach {
//    includeBuild("./kraken-core/$it") {
//        dependencySubstitution {
//            substitute(module("com.icure.kraken:common-$it")).using(project(":"))
//        }
//    }
//}