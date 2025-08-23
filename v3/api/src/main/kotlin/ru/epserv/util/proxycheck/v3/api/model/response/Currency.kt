package ru.epserv.util.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import ru.epserv.util.proxycheck.v3.api.util.mapCodec

data class Currency(
    val code: String,
    val name: String,
    val symbol: String,
) {
    companion object {
        val CODEC = mapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("code").forGetter(Currency::code),
                Codec.STRING.fieldOf("name").forGetter(Currency::name),
                Codec.STRING.fieldOf("symbol").forGetter(Currency::symbol),
            ).apply(instance, ::Currency)
        }
    }
}
