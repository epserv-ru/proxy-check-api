package ru.epserv.util.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import ru.epserv.util.proxycheck.v3.api.util.mapCodec

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
    val address: String,
    val subnet: String,
) {
    companion object {
        @ApiStatus.Internal
        internal val CODEC = mapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("address").forGetter(DeviceEstimate::address),
                Codec.STRING.fieldOf("subnet").forGetter(DeviceEstimate::subnet),
            ).apply(instance, ::DeviceEstimate)
        }
    }
}
