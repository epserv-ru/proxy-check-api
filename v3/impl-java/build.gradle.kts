plugins {
    `kotlin-conventions`
}

group = "${rootProject.group}.v3"

dependencies {
    api(project(":proxy-check-v3-api"))
}

val includeVersionInfoTask = tasks.register<IncludeVersionInfoTask>("includeVersionInfo") {
    specificationVersion = "v3:11-February-2026"
    outputFile = layout.buildDirectory.file("generated/resources/version_info.json")
}

tasks.processResources {
    dependsOn(includeVersionInfoTask)
    from(includeVersionInfoTask.map { it.outputFile }) {
        into("ru/epserv/proxycheck/v3/impl")
    }
}

