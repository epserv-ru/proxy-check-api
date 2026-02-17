package ru.epserv.proxycheck.v3.impl

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import ru.epserv.proxycheck.v3.api.util.VersionInfo
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

    companion object {
        private val TIMESTAMP_CODEC = Codec.STRING.xmap(Instant::parse, Instant::toString)

        val CODEC: Codec<VersionInfoImpl> = RecordCodecBuilder.mapCodec {
            it.group(
                Codec.STRING.fieldOf("implementation_name").forGetter(VersionInfoImpl::implementationName),
                Codec.STRING.fieldOf("implementation_version").forGetter(VersionInfoImpl::implementationVersion),
                Codec.STRING.fieldOf("implementation_vendor").forGetter(VersionInfoImpl::implementationVendor),
                Codec.STRING.fieldOf("specification_name").forGetter(VersionInfoImpl::specificationName),
                Codec.STRING.fieldOf("specification_version").forGetter(VersionInfoImpl::specificationVersion),
                Codec.STRING.fieldOf("specification_vendor").forGetter(VersionInfoImpl::specificationVendor),
                Codec.STRING.fieldOf("git_branch").forGetter(VersionInfoImpl::gitBranch),
                Codec.STRING.fieldOf("git_commit").forGetter(VersionInfoImpl::gitCommit),
                TIMESTAMP_CODEC.fieldOf("git_timestamp").forGetter(VersionInfoImpl::gitTimestamp),
                Codec.STRING.fieldOf("contact_website").forGetter(VersionInfoImpl::contactWebsite),
                Codec.STRING.fieldOf("contact_email").forGetter(VersionInfoImpl::contactEmail),
            ).apply(it, ::VersionInfoImpl)
        }.codec()
    }
}
