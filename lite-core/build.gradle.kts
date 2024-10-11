import com.github.jk1.license.render.CsvReportRenderer
import com.github.jk1.license.render.ReportRenderer
import org.icure.task.CleanCouchDockerTask
import org.icure.task.StartCouchDockerTask
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.text.SimpleDateFormat
import java.util.*

val repoUsername: String by project
val repoPassword: String by project
val mavenReleasesRepository: String by project
val kmapVersion = "0.1.52-main.8d4a565b58"

plugins {
    id("com.icure.kotlin-application-conventions")
    kotlin("plugin.serialization")

    alias(coreLibs.plugins.springBootPlugin) apply(true)
    alias(coreLibs.plugins.springBootDependenciesManagement) apply(true)
    alias(coreLibs.plugins.kotlinAllOpen) apply(true)
    alias(coreLibs.plugins.kotlinSpring) apply(true)
    alias(coreLibs.plugins.gitVersion) apply(true)
    alias(coreLibs.plugins.licenceReport) apply(true)
    // alias(coreLibs.plugins.mavenRepository) apply(true)

    alias(liteLibs.plugins.sonarqube) apply(true)
//    alias(liteLibs.plugins.dockerJava) apply(true)

    `maven-publish`
}

licenseReport {
    renderers = arrayOf<ReportRenderer>(CsvReportRenderer())
}

sonarqube {
    properties {
        property("sonar.projectKey", "icure-io_icure-kotlin-sdk")
        property("sonar.organization", "icure-io")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

val gitVersion: String? by project

group = "org.taktik.icure"
version = gitVersion ?: "0.0.1-SNAPSHOT"

tasks.withType<BootJar> {
    mainClass.set("org.taktik.icure.ICureBackendApplicationKt")
    manifest {
        attributes(mapOf(
            "Built-By" to System.getProperties()["user.name"],
            "Build-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(Date()),
            "Build-Revision" to gitVersion,
            "Created-By" to "Gradle ${gradle.gradleVersion}",
            "Build-Jdk" to "${System.getProperties()["java.version"]} (${System.getProperties()["java.vendor"]} ${System.getProperties()["java.vm.version"]})",
            "Build-OS" to "${System.getProperties()["os.name"]} ${System.getProperties()["os.arch"]} ${System.getProperties()["os.version"]}"
        ))
    }
}

tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
    if ( project.hasProperty("jvmArgs") ) {
        jvmArgs = (project.getProperties()["jvmArgs"] as String).split(Regex("\\s+"))
    }
}

//configure<com.taktik.gradle.plugins.flowr.DockerJavaPluginExtension> {
//    imageRepoAndName = "taktik/kraken"
//}

configurations {
    all {
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
        exclude(group = "log4j", module = "log4j")
    }
    listOf(apiElements, runtimeElements).forEach {
        it.get().outgoing.artifacts.removeIf {
            it.buildDependencies.getDependencies(null).any { it is Jar }
        }
        it.get().outgoing.artifact(tasks.withType<BootJar>().first())
    }
}

// This is needed for the dependency resolution of the kmehr module, when present.
configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("kmehr-module.kraken-common:core")).using(project(":kraken-common:core"))
        substitute(module("org.taktik.icure:logic")).using(project(":kraken-common:logic"))
        substitute(module("org.taktik.icure:mapper")).using(project(":kraken-common:mapper"))
        substitute(module("org.taktik.icure:dto")).using(project(":kraken-common:dto"))
        substitute(module("org.taktik.icure:dao")).using(project(":kraken-common:dao"))
        substitute(module("org.taktik.icure:domain")).using(project(":kraken-common:domain"))
        substitute(module("org.taktik.icure:utils")).using(project(":kraken-common:utils"))
    }
}

val cleanCouchTask = tasks.register<CleanCouchDockerTask>("cleanCouchTask")
val startCouchTask = tasks.register<StartCouchDockerTask>("startCouchTask")

tasks.getByName("test") {
    dependsOn(startCouchTask)
    finalizedBy(cleanCouchTask)
}

tasks.withType<Test> {
    useJUnitPlatform()
    minHeapSize = "512m"
    maxHeapSize = "16g"
}

publishing {
    publications {
        create<MavenPublication>("kraken-lite") {
            artifactId = "kraken-lite"
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "Taktik"
            url = uri(mavenReleasesRepository)
            credentials {
                username = repoUsername
                password = repoPassword
            }
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.isIncremental = true
}

tasks.withType<JavaCompile> {
    options.isFork = true
    options.fork("memoryMaximumSize" to "4096m")
}

tasks.withType<PublishToMavenRepository> {
    doFirst {
        println("Artifact >>> ${project.group}:${project.name}:${project.version} <<< published to Maven repository")
    }
}

tasks.withType<GenerateMavenPom>().all {
    doLast {
        val file = File("./lite-core/build/publications/kraken-lite/pom-default.xml")
        var text = file.readText()
        val regex = "(?s)(<dependencyManagement>.+?<dependencies>)(.+?)(</dependencies>.+?</dependencyManagement>)".toRegex()
        val matcher = regex.find(text)
        if (matcher != null) {
            text = regex.replaceFirst(text, "")
            val firstDeps = matcher.groups[2]!!.value
            text = regex.replaceFirst(text, "$1$2$firstDeps$3")
        }
        file.writeText(text)
    }
}

tasks.withType<BootJar> {
    mainClass.set("org.taktik.icure.ICureBackendApplicationKt")
    manifest {
        attributes(mapOf(
            "Built-By"        to System.getProperties()["user.name"],
            "Build-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(Date()),
            "Build-Revision"  to gitVersion,
            "Created-By"      to "Gradle ${gradle.gradleVersion}",
            "Build-Jdk"       to "${System.getProperties()["java.version"]} (${System.getProperties()["java.vendor"]} ${System.getProperties()["java.vm.version"]})",
            "Build-OS"        to "${System.getProperties()["os.name"]} ${System.getProperties()["os.arch"]} ${System.getProperties()["os.version"]}"
        ))
    }
}


dependencies {
    implementation(project(":kraken-common:logic"))
    implementation(project(":kraken-common:mapper"))
    implementation(project(":kraken-common:dto"))
    implementation(project(":kraken-common:domain"))
    implementation(project(":kraken-common:dao"))
    implementation(project(":kraken-common:jwt"))
    implementation(project(":kraken-common:utils"))
    implementation(project(":kraken-common:core"))
    implementation(project(":kraken-common:service"))

    injectOptionalJars()

    implementation(coreLibs.bundles.springBootLibs)
    implementation(coreLibs.bundles.jacksonLibs)
    implementation(coreLibs.bundles.springLibs)
    implementation(coreLibs.bundles.springSecurityLibs)
    implementation(coreLibs.bundles.krouchLibs)
    implementation(coreLibs.bundles.commonsLibs)
    implementation(coreLibs.bundles.kotlinxCoroutinesLibs)
    implementation(coreLibs.bundles.swaggerLibs)

    implementation(coreLibs.bundles.jsonWebTokenLibs) {
        exclude(group = "com.fasterxml.jackson.core")
    }
    implementation(coreLibs.bundles.bouncyCastleLibs)

//    implementation(liteLibs.bundles.reactorLibs)

    implementation(coreLibs.kotlinxCollectionsImmutableJvm)
    implementation(coreLibs.springSession)
    implementation(coreLibs.mapperProcessor)
    implementation(coreLibs.gcpAuthProvider)
    implementation(coreLibs.taktikBoot)
    implementation(coreLibs.caffeine)
    implementation(coreLibs.mapstruct)
    implementation(coreLibs.libRecur)
    implementation("io.netty:netty-resolver-dns-native-macos:4.1.72.Final:osx-aarch_64")
    implementation(coreLibs.googleApiClient)

    testImplementation(coreLibs.jupiter)
    testImplementation(coreLibs.mockk)
    testImplementation(coreLibs.springBootTest)
    testImplementation(coreLibs.springmockk)
    testImplementation(coreLibs.betterParse)
    testImplementation(coreLibs.icureTestSetup)
    testImplementation(coreLibs.reflections)
    testImplementation(coreLibs.kotlinxSerialization)
    testImplementation(coreLibs.kotlinxCoroutinesTest)

    testImplementation(coreLibs.bundles.kotestLibs)
    testImplementation(coreLibs.bundles.hibernateValidatorLibs)
}

fun DependencyHandlerScope.injectOptionalJars() {
    val regions = System.getProperty("icure.optional.regions")?.lowercase()?.split(",") ?: emptyList()
    if (true || regions.contains("be")) {
        implementation(liteLibs.samModule)
        implementation(liteLibs.kmehrModule)
        implementation(liteLibs.bundles.kmehrDependencies)
    }
}
