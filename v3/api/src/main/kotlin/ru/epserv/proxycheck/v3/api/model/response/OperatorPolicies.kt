package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.util.buildMapCodec
import ru.epserv.proxycheck.v3.api.util.codec.Codecs.forNullableGetter
import java.util.*
import kotlin.jvm.optionals.getOrNull

/**
 * VPN/proxy operator policies.
 *
 * @property adFiltering whether the operator provides ad filtering
 * @property freeAccess whether the operator provides free access
 * @property paidAccess whether the operator provides paid access
 * @property portForwarding whether the operator provides port forwarding
 * @property logging whether the operator retains logs
 * @property anonymousPayments whether the operator accepts anonymous payments
 * @property cryptoPayments whether the operator accepts cryptocurrency payments
 * @property traceableOwnership whether the operator has traceable ownership
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
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
        @ApiStatus.Internal
        val CODEC = buildMapCodec { instance ->
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
