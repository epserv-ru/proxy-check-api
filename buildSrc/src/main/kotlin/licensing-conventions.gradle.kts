import com.diffplug.gradle.spotless.FormatExtension
import com.diffplug.spotless.generic.LicenseHeaderStep
import com.diffplug.spotless.kotlin.KotlinConstants

plugins {
    alias(libs.plugins.spotless)
}

spotless {
    kotlin {
        target("src/**/*.kt")
        setupLicensing(this, KotlinConstants.LICENSE_HEADER_DELIMITER)
    }

    kotlinGradle {
        target("*.gradle.kts")
        setupLicensing(this, "(${KotlinConstants.LICENSE_HEADER_DELIMITER}|plugins \\{)")
    }
}

fun setupLicensing(extension: FormatExtension, headerDelimiter: String) {
    extension.addStep(
        LicenseHeaderStep
            .headerDelimiter(
                providers
                    .fileContents(rootProject.layout.projectDirectory.file("config/license-header.txt"))
                    .asText
                    .map { it.trimEnd() + "\n\n" }::get,
                headerDelimiter,
            )
            .withYearMode(LicenseHeaderStep.YearMode.SET_FROM_GIT)
            .build(),
    )
}
