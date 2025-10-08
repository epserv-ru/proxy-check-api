import io.papermc.paperweight.util.Git
import io.papermc.paperweight.util.path

plugins {
    alias(libs.plugins.kotlin.jvm)

    // for version info in the jar manifest
    alias(libs.plugins.paperweight.patcher) apply false
}

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.serialization.json)
    api(libs.mojang.serialization)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.jar {
    manifest {
        val git = Git(rootProject.layout.projectDirectory.path)
        val gitCommit = git.exec(providers, "rev-parse", "--short=7", "HEAD").get().trim()
        val gitTimestamp = git.exec(providers, "show", "-s", "--format=%cI", gitCommit).get().trim()
        val gitBranch = git.exec(providers, "rev-parse", "--abbrev-ref", "HEAD").get().trim()
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "ElectroPlay Development Team",
            "Specification-Title" to "proxycheck.io",
            "Specification-Version" to "v3",
            "Specification-Vendor" to "https://proxycheck.io/api/",
            "Git-Branch" to gitBranch,
            "Git-Commit" to gitCommit,
            "Git-Timestamp" to gitTimestamp,
            "Implementation-SCM-Branch" to gitBranch,
            "Implementation-SCM-Revision" to gitCommit,
            "Implementation-SCM-Timestamp" to gitTimestamp,
            "Contact-Website" to "https://epserv.ru/",
            "Contact-Email" to "admin@epserv.ru",
        )
    }
}
