package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.util.buildMapCodec

/**
 * Device count estimation.
 *
 * @property address estimated number of devices with this address
 * @property subnet estimated number of devices in this subnet
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
data class DeviceEstimate(
    val address: Long,
    val subnet: Long,
) {
    companion object {
        @ApiStatus.Internal
        val CODEC = buildMapCodec { instance ->
            instance.group(
                Codec.LONG.fieldOf("address").forGetter(DeviceEstimate::address),
                Codec.LONG.fieldOf("subnet").forGetter(DeviceEstimate::subnet),
            ).apply(instance, ::DeviceEstimate)
        }
    }
}
