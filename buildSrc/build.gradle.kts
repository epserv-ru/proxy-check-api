import org.gradle.api.internal.artifacts.DefaultModuleIdentifier
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultMutableVersionConstraint

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.plugins.paperweight.patcher.asModuleDependency())
}

private fun Provider<PluginDependency>.asModuleDependency() = this.map {
    DefaultExternalModuleDependency(
        DefaultModuleIdentifier.newId(it.pluginId, "${it.pluginId}.gradle.plugin"),
        DefaultMutableVersionConstraint(it.version),
        null,
    )
}
