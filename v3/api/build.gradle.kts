/*
 * Copyright (c) 2025-2026 ElectroPlay
 * SPDX-License-Identifier: MPL-2.0
 */

plugins {
    `kotlin-conventions`
    `licensing-conventions`
    `publishing-conventions`
}

group = "${rootProject.group}.v3"

dependencies {
    implementation(libs.kotlin.reflect)
}
