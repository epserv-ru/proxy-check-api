package ru.epserv.proxycheck.v3.api.model.response

import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.util.buildMapCodec
import ru.epserv.proxycheck.v3.api.util.codec.Codecs
import ru.epserv.proxycheck.v3.api.util.codec.Codecs.associatedWith
import ru.epserv.proxycheck.v3.api.util.codec.Codecs.forNullableGetter
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Results for a single IP address.
 *
 * @property network network information
 * @property location location information
 * @property deviceEstimate device number estimate
 * @property detections detections
 * @property operator operator information (may be null)
 * @property lastUpdatedAt timestamp of this result's last update
 * @since 1.0.0
 * @author metabrix
 */
@OptIn(ExperimentalTime::class)
@ApiStatus.AvailableSince("1.0.0")
data class AddressResult(
    val network: Network,
    val location: Location,
    val deviceEstimate: DeviceEstimate,
    val detections: Detections,
    val operator: Operator?,
    val lastUpdatedAt: Instant,
) {
    constructor(
        network: Network,
        location: Location,
        deviceEstimate: DeviceEstimate,
        detections: Detections,
        operator: Optional<Operator>,
        lastUpdatedAt: Instant,
    ) : this(
        network = network,
        location = location,
        deviceEstimate = deviceEstimate,
        detections = detections,
        operator = operator.getOrNull(),
        lastUpdatedAt = lastUpdatedAt,
    )

    companion object {
        @ApiStatus.Internal
        internal val CODEC = buildMapCodec { instance ->
            instance.group(
                Network.CODEC.fieldOf("network").forGetter(AddressResult::network),
                Location.CODEC.fieldOf("location").forGetter(AddressResult::location),
                DeviceEstimate.CODEC.fieldOf("device_estimate").forGetter(AddressResult::deviceEstimate),
                Detections.CODEC.fieldOf("detections").forGetter(AddressResult::detections),
                Operator.CODEC.optionalFieldOf("operator").forNullableGetter(AddressResult::operator),
                Codecs.INSTANT_ISO_8601.fieldOf("last_updated").forGetter(AddressResult::lastUpdatedAt),
            ).apply(instance, ::AddressResult)
        }

        @ApiStatus.Internal
        internal val IP_STRING_TO_RESULT_CODEC = Codecs.INET_ADDRESS_STRING.associatedWith(CODEC).codec()
    }
}
