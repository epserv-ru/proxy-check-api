package ru.epserv.util.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import ru.epserv.util.proxycheck.v3.api.util.codec.Codecs
import ru.epserv.util.proxycheck.v3.api.util.codec.Codecs.associatedWith
import ru.epserv.util.proxycheck.v3.api.util.codec.Codecs.forNullableGetter
import ru.epserv.util.proxycheck.v3.api.util.mapCodec
import java.util.Optional

data class AddressResult(
    val network: Network,
    val location: Location,
    val deviceEstimate: DeviceEstimate,
    val detections: Detections,
    val operator: Operator,
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
        val CODEC = mapCodec { instance ->
            instance.group(
                Network.CODEC.fieldOf("network").forGetter(AddressResult::network),
                Location.CODEC.fieldOf("location").forGetter(AddressResult::location),
                DeviceEstimate.CODEC.fieldOf("device_estimate").forGetter(AddressResult::deviceEstimate),
                Detections.CODEC.fieldOf("detections").forGetter(AddressResult::detections),
                Operator.CODEC.optionalFieldOf("operator").forNullableGetter(AddressResult::operator),
                Codec.LONG.fieldOf("query_time").forGetter(AddressResult::queryTime),
            ).apply(instance, ::AddressResult)
        }

        val MULTIPLE_MAP_CODEC = Codecs.INET_ADDRESS_STRING.associatedWith(CODEC)
    }
}
