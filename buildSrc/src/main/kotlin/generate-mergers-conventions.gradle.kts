import org.icure.task.GenerateMergersFromJsonTask

val generateMergersFromJsonTask = tasks.register<GenerateMergersFromJsonTask>("generateMergersFromJson") {
	inputFolder.set(layout.buildDirectory.dir("generated/ksp/main/resources"))
	outputFolder.set(layout.buildDirectory.dir("generated/ksp/main/kotlin"))

	dependsOn("kspKotlin")
}

// afterEvaluate is fundamental: the kspKotlin task does not exist yet when the script is evaluated, and so the
// finalizedBy cannot be applied otherwise.
afterEvaluate {
	tasks.named("kspKotlin") {
		finalizedBy(generateMergersFromJsonTask)
	}
}

// Also super important: compile must happen after the generation or the generated classes will not be compiled
tasks.named("compileKotlin") {
	dependsOn(generateMergersFromJsonTask)
}