plugins {
    alias(libs.plugins.kotlin.jvm)

    alias(libs.plugins.vanniktech.maven.publish)
    signing
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

signing {
    useGpgCmd()
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    pom {
        val repoUrl = "github.com/epserv-ru/proxy-check-api"

        name = "Kotlin/JVM implementation of proxycheck.io API spec"
        url = repoUrl

        licenses {
            license {
                name = "LGPL-3.0-or-later"
                url = "https://$repoUrl/blob/main/LICENSE"
                distribution = "repo"
            }
        }

        issueManagement {
            system = "GitHub"
            url = "https://$repoUrl/issues"
        }

        developers {
            developer {
                id = "metabrixkt"
                name = "metabrix"
                email = "admin@epserv.ru"
                url = "https://github.com/metabrixkt"
            }
        }

        scm {
            url = "https://$repoUrl"
            connection = "scm:git:ssh://$repoUrl.git"
            developerConnection = connection
        }
    }
}
