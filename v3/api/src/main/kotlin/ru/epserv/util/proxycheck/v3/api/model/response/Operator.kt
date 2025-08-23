package ru.epserv.util.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import ru.epserv.util.proxycheck.v3.api.util.codec.Codecs.setOf
import ru.epserv.util.proxycheck.v3.api.util.mapCodec

/**
 * VPN/proxy operator information.
 *
 * @property name operator name
 * @property url operator website URL
 * @property anonymity operator anonymity level (if known)
 * @property popularity operator popularity level (if known)
 * @property protocols supported protocols by the operator
 * @property policies operator policies
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
data class Operator(
    val name: String,
    val url: String,
    val anonymity: String?,
    val popularity: String?,
    val protocols: Set<String>,
    val policies: OperatorPolicies,
) {
    companion object {
        @ApiStatus.Internal
        internal val CODEC = mapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("name").forGetter(Operator::name),
                Codec.STRING.fieldOf("url").forGetter(Operator::url),
                Codec.STRING.optionalFieldOf("anonymity", "unknown").forGetter(Operator::anonymity),
                Codec.STRING.optionalFieldOf("popularity", "unknown").forGetter(Operator::popularity),
                Codec.STRING.setOf().fieldOf("protocols").forGetter(Operator::protocols),
                OperatorPolicies.CODEC.optionalFieldOf("policies", OperatorPolicies.UNKNOWN).forGetter(Operator::policies),
            ).apply(instance, ::Operator)
        }
    }
}
