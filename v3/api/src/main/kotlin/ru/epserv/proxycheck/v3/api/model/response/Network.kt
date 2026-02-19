/*
 * Copyright (c) 2025-2026 ElectroPlay
 * SPDX-License-Identifier: MPL-2.0
 */

package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.model.common.CidrIpRange
import ru.epserv.proxycheck.v3.api.util.buildMapCodec
import ru.epserv.proxycheck.v3.api.util.codec.Codecs
import ru.epserv.proxycheck.v3.api.util.codec.Codecs.forNullableGetter
import java.util.*
import kotlin.jvm.optionals.getOrNull

/**
 * Network information.
 *
 * @property asn autonomous system number
 * @property range IP range in CIDR notation
 * @property hostName host name
 * @property provider network provider
 * @property organisation organisation name
 * @property type network type
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
data class Network(
    val asn: Int?,
    val range: CidrIpRange?,
    val hostName: String?,
    val provider: String?,
    val organisation: String?,
    val type: String,
) {
    constructor(
        asn: Optional<Int>,
        range: Optional<CidrIpRange>,
        hostName: Optional<String>,
        provider: Optional<String>,
        organisation: Optional<String>,
        type: String,
    ) : this(
        asn = asn.getOrNull(),
        range = range.getOrNull(),
        hostName = hostName.getOrNull(),
        provider = provider.getOrNull(),
        organisation = organisation.getOrNull(),
        type = type,
    )

    companion object {
        @ApiStatus.Internal
        val CODEC = buildMapCodec { instance ->
            instance.group(
                Codecs.ASN_STRING.optionalFieldOf("asn").forNullableGetter(Network::asn),
                CidrIpRange.STRING_CODEC.optionalFieldOf("range").forNullableGetter(Network::range),
                Codec.STRING.optionalFieldOf("hostname").forNullableGetter(Network::hostName),
                Codec.STRING.optionalFieldOf("provider").forNullableGetter(Network::provider),
                Codec.STRING.optionalFieldOf("organisation").forNullableGetter(Network::organisation),
                Codec.STRING.fieldOf("type").forGetter(Network::type),
            ).apply(instance, ::Network)
        }
    }
}
