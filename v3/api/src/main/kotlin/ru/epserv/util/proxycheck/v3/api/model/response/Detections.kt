package ru.epserv.util.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.Range
import ru.epserv.util.proxycheck.v3.api.util.mapCodec

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
) {
    companion object {
        @ApiStatus.Internal
        internal val CODEC = mapCodec { instance ->
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
