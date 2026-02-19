/*
 * Copyright (c) 2026 ElectroPlay
 * SPDX-License-Identifier: MPL-2.0
 */

package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus

/**
 * Type of service an operator offers.
 *
 * Operator service types are not standardized. New ones may be introduced (and existing ones may be
 * removed) within the same API version, which is the reason why [OperatorService] is not an enum
 * class.
 *
 * @property id operator service identifier
 * @since 1.0.0
 * @author metabrix
 * @see [OperatorService.fromId]
 */
@ConsistentCopyVisibility
@ApiStatus.AvailableSince("1.0.0")
data class OperatorService private constructor(val id: String) {
    companion object {
        private val cache = mutableMapOf<String, OperatorService>()

        fun fromId(id: String): OperatorService {
            return synchronized(cache) { cache.getOrPut(id) { OperatorService(id) } }
        }

        @ApiStatus.Internal
        val CODEC: Codec<OperatorService> = Codec.STRING.xmap(::fromId, OperatorService::id)

        val RESIDENTIAL_PROXIES = fromId("residential_proxies")

        val WIRELESS_PROXIES = fromId("wireless_proxies")

        val DATACENTER_PROXIES = fromId("datacenter_proxies")

        val DATACENTER_VPNS = fromId("datacenter_vpns")

        val RESIDENTIAL_VPNS = fromId("residential_vpns")

        val WEB_SCRAPING = fromId("web_scraping")
    }
}
