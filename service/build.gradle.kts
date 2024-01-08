plugins {
    id("com.icure.kotlin-library-conventions")

    alias(coreLibs.plugins.kotlinAllOpen) apply(true)
    alias(coreLibs.plugins.kotlinSpring) apply(true)
}

dependencies {
    implementation(project(":kraken-common:domain"))
    implementation(project(":kraken-common:logic"))
    implementation(project(":kraken-common:utils"))

    implementation(coreLibs.bundles.kotlinxCoroutinesLibs)
    implementation(coreLibs.bundles.springBootLibs)
    implementation(coreLibs.bundles.krouchLibs)
}