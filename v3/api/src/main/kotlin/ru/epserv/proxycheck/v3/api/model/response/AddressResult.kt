package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.util.codec.Codecs
import ru.epserv.proxycheck.v3.api.util.codec.Codecs.associatedWith
import ru.epserv.proxycheck.v3.api.util.codec.Codecs.forNullableGetter
import ru.epserv.proxycheck.v3.api.util.mapCodec
import java.util.*

/**
 * Results for a single IP address.
 *
 * @property network network information
 * @property location location information
 * @property deviceEstimate device number estimate
 * @property detections detections
 * @property operator operator information (may be null)
 * @property queryTime time taken to process the query in milliseconds, excluding network RTT
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
data class AddressResult(
    val network: Network,
    val location: Location,
    val deviceEstimate: DeviceEstimate,
    val detections: Detections,
    val operator: Operator?,
    val queryTime: Long,
) {
    constructor(
        network: Network,
        location: Location,
        deviceEstimate: DeviceEstimate,
        detections: Detections,
        operator: Optional<Operator>,
        queryTime: Long,
    ) : this(
        network = network,
        location = location,
        deviceEstimate = deviceEstimate,
        detections = detections,
        operator = operator.orElse(null),
        queryTime = queryTime,
    )

    companion object {
        @ApiStatus.Internal
        internal val CODEC = mapCodec { instance ->
            instance.group(
                Network.CODEC.fieldOf("network").forGetter(AddressResult::network),
                Location.CODEC.fieldOf("location").forGetter(AddressResult::location),
                DeviceEstimate.CODEC.fieldOf("device_estimate").forGetter(AddressResult::deviceEstimate),
                Detections.CODEC.fieldOf("detections").forGetter(AddressResult::detections),
                Operator.CODEC.optionalFieldOf("operator").forNullableGetter(AddressResult::operator),
                Codec.LONG.fieldOf("query_time").forGetter(AddressResult::queryTime),
            ).apply(instance, ::AddressResult)
        }

        @ApiStatus.Internal
        internal val MULTIPLE_MAP_CODEC = Codecs.INET_ADDRESS_STRING.associatedWith(CODEC)
    }
}
