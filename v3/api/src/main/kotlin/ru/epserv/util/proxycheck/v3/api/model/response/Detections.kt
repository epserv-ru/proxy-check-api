package ru.epserv.util.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.Range
import ru.epserv.util.proxycheck.v3.api.util.mapCodec

data class Detections(
    val proxy: Boolean,
    val vpn: Boolean,
    val compromised: Boolean,
    val scraper: Boolean,
    val tor: Boolean,
    val hosting: Boolean,
    val anonymous: Boolean,
    val risk: @Range(from = 0, to = 100) Int,
) {
    companion object {
        val CODEC = mapCodec { instance ->
            instance.group(
                Codec.BOOL.optionalFieldOf("proxy", false).forGetter(Detections::proxy),
                Codec.BOOL.optionalFieldOf("vpn", false).forGetter(Detections::vpn),
                Codec.BOOL.optionalFieldOf("compromised", false).forGetter(Detections::compromised),
                Codec.BOOL.optionalFieldOf("scraper", false).forGetter(Detections::scraper),
                Codec.BOOL.optionalFieldOf("tor", false).forGetter(Detections::tor),
                Codec.BOOL.optionalFieldOf("hosting", false).forGetter(Detections::hosting),
                Codec.BOOL.optionalFieldOf("anonymous", false).forGetter(Detections::anonymous),
                Codec.intRange(0, 100).optionalFieldOf("risk", 0).forGetter(Detections::risk),
            ).apply(instance, ::Detections)
        }
    }
}
