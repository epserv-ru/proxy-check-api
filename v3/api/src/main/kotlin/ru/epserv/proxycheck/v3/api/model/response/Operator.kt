package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.util.buildMapCodec
import ru.epserv.proxycheck.v3.api.util.codec.Codecs.forNullableGetter
import ru.epserv.proxycheck.v3.api.util.codec.Codecs.setOf
import java.util.*
import kotlin.jvm.optionals.getOrNull

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
    constructor(
        name: String,
        url: String,
        anonymity: Optional<String>,
        popularity: Optional<String>,
        protocols: Set<String>,
        policies: OperatorPolicies,
    ) : this(
        name = name,
        url = url,
        anonymity = anonymity.getOrNull(),
        popularity = popularity.getOrNull(),
        protocols = protocols,
        policies = policies,
    )

    companion object {
        @ApiStatus.Internal
        val CODEC = buildMapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("name").forGetter(Operator::name),
                Codec.STRING.fieldOf("url").forGetter(Operator::url),
                Codec.STRING.optionalFieldOf("anonymity").forNullableGetter(Operator::anonymity),
                Codec.STRING.optionalFieldOf("popularity").forNullableGetter(Operator::popularity),
                Codec.STRING.setOf().fieldOf("protocols").forGetter(Operator::protocols),
                OperatorPolicies.CODEC.optionalFieldOf("policies", OperatorPolicies.UNKNOWN).forGetter(Operator::policies),
            ).apply(instance, ::Operator)
        }
    }
}
