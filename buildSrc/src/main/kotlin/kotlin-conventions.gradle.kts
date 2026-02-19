plugins {
    alias(libs.plugins.kotlin.jvm)
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
