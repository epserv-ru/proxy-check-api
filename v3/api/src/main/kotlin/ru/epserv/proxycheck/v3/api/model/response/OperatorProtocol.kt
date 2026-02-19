/*
 * Copyright (c) 2026 ElectroPlay
 * SPDX-License-Identifier: MPL-2.0
 */

package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus

/**
 * A protocol an operator offers their services through.
 *
 * Operator protocols are not standardized. New ones may be introduced (and existing ones may be
 * removed) within the same API version, which is the reason why [OperatorProtocol] is not an enum
 * class.
 *
 * @property id operator protocol identifier
 * @since 1.0.0
 * @author metabrix
 * @see [OperatorProtocol.fromId]
 */
@ConsistentCopyVisibility
@ApiStatus.AvailableSince("1.0.0")
data class OperatorProtocol private constructor(val id: String) {
    companion object {
        private val cache = mutableMapOf<String, OperatorProtocol>()

        fun fromId(id: String): OperatorProtocol {
            return synchronized(cache) { cache.getOrPut(id) { OperatorProtocol(id) } }
        }

        @ApiStatus.Internal
        val CODEC: Codec<OperatorProtocol> = Codec.STRING.xmap(::fromId, OperatorProtocol::id)

        val OPENVPN = fromId("OpenVPN")

        val WIREGUARD = fromId("Wireguard")

        val ONION_ROUTING = fromId("Onion Routing")

        val IPSEC = fromId("IPSec")

        val SSH2 = fromId("SSH2")

        val PPTP = fromId("PPTP")

        val L2TP = fromId("L2TP")

        val SOCKS5 = fromId("SOCKS5")

        val IKEV2 = fromId("IKEv2")

        val HTTP = fromId("HTTP")

        val HTTPS = fromId("HTTPS")

        val SOCKS4 = fromId("SOCKS4")
    }
}
