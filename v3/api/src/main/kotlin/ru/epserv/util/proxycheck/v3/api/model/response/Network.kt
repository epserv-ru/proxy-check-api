package ru.epserv.util.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import ru.epserv.util.proxycheck.v3.api.model.common.CidrIpRange
import ru.epserv.util.proxycheck.v3.api.util.codec.Codecs
import ru.epserv.util.proxycheck.v3.api.util.mapCodec

data class Network(
    val asn: Int,
    val range: CidrIpRange,
    val hostName: String,
    val provider: String,
    val organisation: String,
    val type: String,
) {
    companion object {
        val CODEC = mapCodec { instance ->
            instance.group(
                Codecs.ASN_STRING.fieldOf("asn").forGetter(Network::asn),
                CidrIpRange.STRING_CODEC.fieldOf("range").forGetter(Network::range),
                Codec.STRING.fieldOf("hostname").forGetter(Network::hostName),
                Codec.STRING.fieldOf("provider").forGetter(Network::provider),
                Codec.STRING.fieldOf("organisation").forGetter(Network::organisation),
                Codec.STRING.fieldOf("type").forGetter(Network::type),
            ).apply(instance, ::Network)
        }
    }
}
