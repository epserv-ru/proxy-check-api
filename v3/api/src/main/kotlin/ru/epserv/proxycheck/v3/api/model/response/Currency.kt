package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.util.mapCodec

/**
 * Currency information.
 *
 * @property code currency code
 * @property name currency name
 * @property symbol currency symbol
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
data class Currency(
    val code: String,
    val name: String,
    val symbol: String,
) {
    companion object {
        @ApiStatus.Internal
        internal val CODEC = mapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("code").forGetter(Currency::code),
                Codec.STRING.fieldOf("name").forGetter(Currency::name),
                Codec.STRING.fieldOf("symbol").forGetter(Currency::symbol),
            ).apply(instance, ::Currency)
        }
    }
}
