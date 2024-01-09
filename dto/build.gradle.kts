plugins {
    id("com.icure.kotlin-library-conventions")

    alias(coreLibs.plugins.kotlinAllOpen) apply(true)
    alias(coreLibs.plugins.mavenRepository)
    alias(coreLibs.plugins.gitVersion)
}

val gitVersion: String? by project

group = "org.taktik.icure"
version = gitVersion ?: "0.0.1-SNAPSHOT"

dependencies {
    if (rootProject.name != "kraken-common") {
        implementation(project(":kraken-common:utils"))
    } else {
        implementation(project(":utils"))
    }

    implementation(coreLibs.bundles.xmlLibs)
    implementation(coreLibs.bundles.jacksonLibs)
    implementation(coreLibs.bundles.swaggerLibs) {
        exclude(group = "com.fasterxml.jackson.core")
        exclude(group = "org.springframework")
    }

    implementation(coreLibs.bundles.commonsLibs)
    implementation(coreLibs.bundles.kotlinxCoroutinesLibs)
    implementation(coreLibs.bundles.krouchLibs)

    implementation(coreLibs.reflections)
    implementation(coreLibs.guava)
}
