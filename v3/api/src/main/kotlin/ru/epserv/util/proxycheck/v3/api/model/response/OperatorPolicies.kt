package ru.epserv.util.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import ru.epserv.util.proxycheck.v3.api.util.codec.Codecs.forNullableGetter
import ru.epserv.util.proxycheck.v3.api.util.mapCodec
import java.util.*
import kotlin.jvm.optionals.getOrNull

data class OperatorPolicies(
    val adFiltering: Boolean?,
    val freeAccess: Boolean?,
    val paidAccess: Boolean?,
    val portForwarding: Boolean?,
    val logging: Boolean?,
    val anonymousPayments: Boolean?,
    val cryptoPayments: Boolean?,
    val traceableOwnership: Boolean?,
) {
    constructor(
        adFiltering: Optional<Boolean>,
        freeAccess: Optional<Boolean>,
        paidAccess: Optional<Boolean>,
        portForwarding: Optional<Boolean>,
        logging: Optional<Boolean>,
        anonymousPayments: Optional<Boolean>,
        cryptoPayments: Optional<Boolean>,
        traceableOwnership: Optional<Boolean>,
    ) : this(
        adFiltering = adFiltering.getOrNull(),
        freeAccess = freeAccess.getOrNull(),
        paidAccess = paidAccess.getOrNull(),
        portForwarding = portForwarding.getOrNull(),
        logging = logging.getOrNull(),
        anonymousPayments = anonymousPayments.getOrNull(),
        cryptoPayments = cryptoPayments.getOrNull(),
        traceableOwnership = traceableOwnership.getOrNull(),
    )

    companion object {
        val CODEC = mapCodec { instance ->
            instance.group(
                Codec.BOOL.optionalFieldOf("ad_filtering").forNullableGetter(OperatorPolicies::adFiltering),
                Codec.BOOL.optionalFieldOf("free_access").forNullableGetter(OperatorPolicies::freeAccess),
                Codec.BOOL.optionalFieldOf("paid_access").forNullableGetter(OperatorPolicies::paidAccess),
                Codec.BOOL.optionalFieldOf("port_forwarding").forNullableGetter(OperatorPolicies::portForwarding),
                Codec.BOOL.optionalFieldOf("logging").forNullableGetter(OperatorPolicies::logging),
                Codec.BOOL.optionalFieldOf("anonymous_payments").forNullableGetter(OperatorPolicies::anonymousPayments),
                Codec.BOOL.optionalFieldOf("crypto_payments").forNullableGetter(OperatorPolicies::cryptoPayments),
                Codec.BOOL.optionalFieldOf("traceable_ownership").forNullableGetter(OperatorPolicies::traceableOwnership),
            ).apply(instance, ::OperatorPolicies)
        }

        val UNKNOWN = OperatorPolicies(
            adFiltering = null,
            freeAccess = null,
            paidAccess = null,
            portForwarding = null,
            logging = null,
            anonymousPayments = null,
            cryptoPayments = null,
            traceableOwnership = null,
        )
    }
}
