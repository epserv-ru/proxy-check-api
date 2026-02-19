plugins {
    alias(libs.plugins.vanniktech.maven.publish)
    signing
}

signing {
    useGpgCmd()
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    pom {
        val repoUrl = "github.com/epserv-ru/proxy-check-api"

        name = project.name
        description = project.description
        url = "https://$repoUrl"
        inceptionYear = "2026"

        licenses {
            license {
                name = "Mozilla Public License 2.0"
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
            connection = "scm:git:ssh://git@$repoUrl.git"
            developerConnection = connection
        }
    }
}
