@file:OptIn(ExperimentalTime::class)

package ru.epserv.proxycheck.v3.impl

import ru.epserv.proxycheck.v3.api.util.VersionInfo
import java.util.jar.Attributes
import java.util.jar.Manifest
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class VersionInfoImpl(
    override val specificationName: String,
    override val specificationVersion: String,
    override val specificationVendor: String,

    override val implementationName: String,
    override val implementationVersion: String,
    override val implementationVendor: String,

    override val gitCommit: String,
    override val gitBranch: String,
    override val gitTimestamp: Instant,

    override val contactWebsite: String,
    override val contactEmail: String,
) : VersionInfo {
    override val httpUserAgent by lazy { "${this.implementationName}/${this.implementationVersion} (+${this.contactWebsite}; <${this.contactEmail}>)" }

    constructor(manifest: Manifest) : this(manifest.mainAttributes)

    constructor(attributes: Attributes) : this(
        specificationName = attributes.getOrThrow("Specification-Title"),
        specificationVersion = attributes.getOrThrow("Specification-Version"),
        specificationVendor = attributes.getOrThrow("Specification-Vendor"),

        implementationName = attributes.getOrThrow("Implementation-Title"),
        implementationVersion = attributes.getOrThrow("Implementation-Version"),
        implementationVendor = attributes.getOrThrow("Implementation-Vendor"),

        gitCommit = attributes.getOrThrow("Git-Commit"),
        gitBranch = attributes.getOrThrow("Git-Branch"),
        gitTimestamp = Instant.parse(attributes.getOrThrow("Git-Timestamp")),

        contactWebsite = attributes.getOrThrow("Contact-Website"),
        contactEmail = attributes.getOrThrow("Contact-Email"),
    )

    companion object {
        private fun Attributes.getOrThrow(name: String): String = requireNotNull(this.getValue(name)) { "Unable to find manifest attribute: $name" }
    }
}
