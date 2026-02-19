/*
 * Copyright (c) 2025-2026 ElectroPlay
 * SPDX-License-Identifier: MPL-2.0
 */

package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.util.buildMapCodec
import ru.epserv.proxycheck.v3.api.util.codec.Codecs
import ru.epserv.proxycheck.v3.api.util.codec.Codecs.associatedWith
import ru.epserv.proxycheck.v3.api.util.codec.Codecs.forNullableGetter
import java.net.InetAddress
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Instant

/**
 * Results for a single IP address.
 *
 * @property network network information
 * @property location location information
 * @property deviceEstimate device number estimate
 * @property detections detections
 * @property detectionHistory detection history (may be null)
 * @property attackHistory attack history (may be null)
 * @property operator operator information (may be null)
 * @property lastUpdatedAt timestamp of this result's last update
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
data class AddressResult(
    val network: Network,
    val location: Location,
    val deviceEstimate: DeviceEstimate,
    val detections: Detections,
    val detectionHistory: DetectionHistory?,
    val attackHistory: Map<AttackType, Int>?,
    val operator: Operator?,
    val lastUpdatedAt: Instant,
) {
    constructor(
        network: Network,
        location: Location,
        deviceEstimate: DeviceEstimate,
        detections: Detections,
        detectionHistory: Optional<DetectionHistory>,
        attackHistory: Optional<Map<AttackType, Int>>,
        operator: Optional<Operator>,
        lastUpdatedAt: Instant,
    ) : this(
        network = network,
        location = location,
        deviceEstimate = deviceEstimate,
        detections = detections,
        detectionHistory = detectionHistory.getOrNull(),
        attackHistory = attackHistory.getOrNull(),
        operator = operator.getOrNull(),
        lastUpdatedAt = lastUpdatedAt,
    )

    companion object {
        @ApiStatus.Internal
        val CODEC = buildMapCodec { instance ->
            instance.group(
                Network.CODEC.fieldOf("network").forGetter(AddressResult::network),
                Location.CODEC.fieldOf("location").forGetter(AddressResult::location),
                DeviceEstimate.CODEC.fieldOf("device_estimate").forGetter(AddressResult::deviceEstimate),
                Detections.CODEC.fieldOf("detections").forGetter(AddressResult::detections),
                DetectionHistory.CODEC.optionalFieldOf("detection_history").forNullableGetter(AddressResult::detectionHistory),
                Codec.unboundedMap(AttackType.CODEC, Codec.INT).optionalFieldOf("attack_history").forNullableGetter(AddressResult::attackHistory),
                Operator.CODEC.optionalFieldOf("operator").forNullableGetter(AddressResult::operator),
                Codecs.INSTANT_ISO_8601.fieldOf("last_updated").forGetter(AddressResult::lastUpdatedAt),
            ).apply(instance, ::AddressResult)
        }

        @ApiStatus.Internal
        val IP_STRING_TO_RESULT_CODEC: Codec<Map<InetAddress, AddressResult>> = Codecs.INET_ADDRESS_STRING.associatedWith(CODEC).codec()
    }
}
