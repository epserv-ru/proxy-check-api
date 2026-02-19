/*
 * Copyright (c) 2025-2026 ElectroPlay
 * SPDX-License-Identifier: MPL-2.0
 */

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "proxy-check-api"

for ((name, path) in mapOf(
    "proxy-check-v3-api" to "v3/api",
    "proxy-check-v3-impl-java" to "v3/impl-java",
)) {
    include(name)
    project(":$name").projectDir = file(path)
}
