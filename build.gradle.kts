plugins {
    base
}

val copyArtifactsTask = tasks.register<Copy>("copyArtifacts") {
    from(subprojects.flatMap { it.tasks.withType<Jar>() })
    into(layout.buildDirectory.dir("libs"))
}

tasks.assemble {
    dependsOn(copyArtifactsTask)
}
