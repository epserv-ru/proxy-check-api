package ru.epserv.util.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import ru.epserv.util.proxycheck.v3.api.util.codec.Codecs.setOf
import ru.epserv.util.proxycheck.v3.api.util.mapCodec

data class Operator(
    val name: String,
    val url: String,
    val anonymity: String?,
    val popularity: String?,
    val protocols: Set<String>,
    val policies: OperatorPolicies,
) {
    companion object {
        val CODEC = mapCodec { instance ->
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
