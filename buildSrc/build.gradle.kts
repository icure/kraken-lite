plugins {
	id("org.jetbrains.kotlin.jvm") version coreLibs.versions.kotlin
}

repositories {
	mavenCentral()
	maven { url = uri("https://maven.taktik.be/content/groups/public") }
	maven { url = uri("https://jitpack.io") }
}

version = "0.0.1-SNAPSHOT"

kotlin {
	jvmToolchain(21)
	compilerOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
	implementation(coreLibs.bundles.kotlinxCoroutinesLibs)
	implementation(group = "org.jetbrains.kotlin.plugin.serialization", name = "org.jetbrains.kotlin.plugin.serialization.gradle.plugin", version = "2.2.0")
}
