package ru.epserv.util.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import ru.epserv.util.proxycheck.v3.api.util.mapCodec

data class DeviceEstimate(
    val address: String,
    val subnet: String,
) {
    companion object {
        val CODEC = mapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("address").forGetter(DeviceEstimate::address),
                Codec.STRING.fieldOf("subnet").forGetter(DeviceEstimate::subnet),
            ).apply(instance, ::DeviceEstimate)
        }
    }
}
