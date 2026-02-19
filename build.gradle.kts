/*
 * Copyright (c) 2025-2026 ElectroPlay
 * SPDX-License-Identifier: MPL-2.0
 */

plugins {
    base
    `licensing-conventions`
}

val copyArtifactsTask = tasks.register<Copy>("copyArtifacts") {
    from(subprojects.flatMap { it.tasks.withType<Jar>() })
    into(layout.buildDirectory.dir("libs"))
}

tasks.assemble {
    dependsOn(copyArtifactsTask)
}
