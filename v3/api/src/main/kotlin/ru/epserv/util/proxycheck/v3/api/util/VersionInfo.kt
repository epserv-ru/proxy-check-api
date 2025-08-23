@file:OptIn(ExperimentalTime::class)

package ru.epserv.util.proxycheck.v3.api.util

import org.jetbrains.annotations.ApiStatus
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * API implementation version info.
 *
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
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
