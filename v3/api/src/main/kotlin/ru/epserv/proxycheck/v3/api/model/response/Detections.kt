/*
 * Copyright (c) 2025-2026 ElectroPlay
 * SPDX-License-Identifier: MPL-2.0
 */

package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.Range
import ru.epserv.proxycheck.v3.api.util.buildMapCodec
import ru.epserv.proxycheck.v3.api.util.codec.Codecs
import ru.epserv.proxycheck.v3.api.util.codec.Codecs.forNullableGetter
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Instant

/**
 * Detections information.
 *
 * @property proxy whether the IP is detected as a proxy
 * @property vpn whether the IP is detected as a VPN
 * @property compromised whether the IP is detected as compromised
 * @property scraper whether the IP is detected as a scraper
 * @property tor whether the IP is detected as a Tor exit node
 * @property hosting whether the IP is detected as a hosting provider
 * @property anonymous whether the IP is detected as anonymous
 * @property risk risk score of the IP (0-100)
 * @property confidence confidence score of the detection (0-100)
 * @property firstSeen the first time the IP was seen acting as a proxy/VPN/etc.,
 *           or `null` if undetected/not available
 * @property lastSeen the last time the IP was seen acting as a proxy/VPN/etc.,
 *           or `null` if undetected/not available
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
data class Detections(
    val proxy: Boolean,
    val vpn: Boolean,
    val compromised: Boolean,
    val scraper: Boolean,
    val tor: Boolean,
    val hosting: Boolean,
    val anonymous: Boolean,
    val risk: @Range(from = 0, to = 100) Int,
    val confidence: @Range(from = 0, to = 100) Int,
    val firstSeen: Instant?,
    val lastSeen: Instant?,
) {
    constructor(
        proxy: Boolean,
        vpn: Boolean,
        compromised: Boolean,
        scraper: Boolean,
        tor: Boolean,
        hosting: Boolean,
        anonymous: Boolean,
        risk: @Range(from = 0, to = 100) Int,
        confidence: @Range(from = 0, to = 100) Int,
        firstSeen: Optional<Instant>,
        lastSeen: Optional<Instant>,
    ) : this(
        proxy = proxy,
        vpn = vpn,
        compromised = compromised,
        scraper = scraper,
        tor = tor,
        hosting = hosting,
        anonymous = anonymous,
        risk = risk,
        confidence = confidence,
        firstSeen = firstSeen.getOrNull(),
        lastSeen = lastSeen.getOrNull(),
    )
    companion object {
        @ApiStatus.Internal
        val CODEC = buildMapCodec { instance ->
            instance.group(
                Codec.BOOL.fieldOf("proxy").forGetter(Detections::proxy),
                Codec.BOOL.fieldOf("vpn").forGetter(Detections::vpn),
                Codec.BOOL.fieldOf("compromised").forGetter(Detections::compromised),
                Codec.BOOL.fieldOf("scraper").forGetter(Detections::scraper),
                Codec.BOOL.fieldOf("tor").forGetter(Detections::tor),
                Codec.BOOL.fieldOf("hosting").forGetter(Detections::hosting),
                Codec.BOOL.fieldOf("anonymous").forGetter(Detections::anonymous),
                Codec.intRange(0, 100).optionalFieldOf("risk", 0).forGetter(Detections::risk),
                Codec.intRange(0, 100).fieldOf("confidence").forGetter(Detections::confidence),
                Codecs.INSTANT_ISO_8601.optionalFieldOf("first_seen").forNullableGetter(Detections::firstSeen),
                Codecs.INSTANT_ISO_8601.optionalFieldOf("last_seen").forNullableGetter(Detections::lastSeen),
            ).apply(instance, ::Detections)
        }
    }
}
