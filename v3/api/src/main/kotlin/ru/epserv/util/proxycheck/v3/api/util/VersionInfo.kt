@file:OptIn(ExperimentalTime::class)

package ru.epserv.util.proxycheck.v3.api.util

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface VersionInfo {
    val specificationName: String
    val specificationVersion: String
    val specificationVendor: String

    val implementationName: String
    val implementationVersion: String
    val implementationVendor: String

    val gitCommit: String
    val gitBranch: String
    val gitTimestamp: Instant

    val contactWebsite: String
    val contactEmail: String

    val httpUserAgent: String
}
