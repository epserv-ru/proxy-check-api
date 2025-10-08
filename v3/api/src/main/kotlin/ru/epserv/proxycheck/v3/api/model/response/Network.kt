package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.model.common.CidrIpRange
import ru.epserv.proxycheck.v3.api.util.codec.Codecs
import ru.epserv.proxycheck.v3.api.util.codec.Codecs.forNullableGetter
import ru.epserv.proxycheck.v3.api.util.mapCodec
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
    val asn: Int,
    val range: CidrIpRange,
    val hostName: String?,
    val provider: String,
    val organisation: String,
    val type: String,
) {
    constructor(
        asn: Int,
        range: CidrIpRange,
        hostName: Optional<String>,
        provider: String,
        organisation: String,
        type: String,
    ) : this(
        asn = asn,
        range = range,
        hostName = hostName.getOrNull(),
        provider = provider,
        organisation = organisation,
        type = type,
    )

    companion object {
        @ApiStatus.Internal
        internal val CODEC = mapCodec { instance ->
            instance.group(
                Codecs.ASN_STRING.fieldOf("asn").forGetter(Network::asn),
                CidrIpRange.STRING_CODEC.fieldOf("range").forGetter(Network::range),
                Codec.STRING.optionalFieldOf("hostname").forNullableGetter(Network::hostName),
                Codec.STRING.fieldOf("provider").forGetter(Network::provider),
                Codec.STRING.fieldOf("organisation").forGetter(Network::organisation),
                Codec.STRING.fieldOf("type").forGetter(Network::type),
            ).apply(instance, ::Network)
        }
    }
}
