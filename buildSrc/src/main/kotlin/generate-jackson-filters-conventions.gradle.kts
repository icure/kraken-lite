import org.gradle.kotlin.dsl.register
import org.icure.task.GenerateJacksonFiltersFromJsonTask
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

val dtoProject = project.parent?.childProjects?.get("dto")
	?: error("Could not locate sibling 'dto' project from ${project.path}")

// The task that emits the JSON descriptors. Reference it by path (not via tasks.named): the ksp
// plugin registers `kspKotlin` lazily, so dto is usually not configured yet when this convention
// is applied to the consuming module, and an eager lookup would fail with "task not found".
// A String task path is resolved later, during task-graph construction.
val dtoKspKotlinPath = "${dtoProject.path}:kspKotlin"

val jacksonFiltersDir = layout.buildDirectory.dir("generated/jackson-filters/main/kotlin")

val generateJacksonFiltersTask =
	tasks.register<GenerateJacksonFiltersFromJsonTask>("generateJacksonFiltersFromJson") {
		group = "generation"
		description = "Generates Jackson filter sources from the dto module's ksp JSON descriptors."

		inputFolder.set(dtoProject.layout.buildDirectory.dir("generated/ksp/main/resources"))
		outputFolder.set(jacksonFiltersDir)

		dependsOn(dtoKspKotlinPath)
	}

extensions.configure<KotlinJvmProjectExtension>("kotlin") {
	sourceSets.named("main") {
		kotlin.srcDir(jacksonFiltersDir)
	}
}

tasks.named("compileKotlin") {
	dependsOn(generateJacksonFiltersTask)
}

afterEvaluate {
	tasks.named("kspKotlin") {
		dependsOn(generateJacksonFiltersTask)
	}
}