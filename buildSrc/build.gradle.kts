plugins {
	id("org.jetbrains.kotlin.jvm") version coreLibs.versions.kotlin
	`kotlin-dsl`
}

repositories {
	mavenCentral()
	maven { url = uri("https://maven.taktik.be/content/groups/public") }
	maven { url = uri("https://jitpack.io") }
	maven { url = uri("https://maven.pkg.github.com/icure/sdk-codegen") }
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

kotlin {
	compilerOptions {
		if (System.getProperty("idea.active") == "true") {
			freeCompilerArgs = listOf("-Xdebug")
		}
	}
}

dependencies {
	implementation(coreLibs.guava)
	implementation(coreLibs.jacksonKotlin)
	implementation(coreLibs.jacksonDatabind)
	implementation(coreLibs.kotlinxSerializationPlugin)
	implementation(coreLibs.kotlinMultiplatformPlugin)
	implementation(coreLibs.kotlinxCoroutinesCore)
	implementation("com.icure:kraken-codegen-library:1.10.0")
}
