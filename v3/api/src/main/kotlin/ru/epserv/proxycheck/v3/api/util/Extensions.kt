package ru.epserv.proxycheck.v3.api.util

import com.mojang.datafixers.kinds.App
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.net.URLEncoder

fun String.urlEncode(): String = URLEncoder.encode(this, Charsets.UTF_8)

internal fun Map<String, String>.toHttpQueryString(): String {
    return this
        .map { (key, value) -> key.urlEncode() to value.urlEncode() }
        .joinToString("&") { (key, value) -> "$key=$value" }
}

internal operator fun <A, B> Pair<A, B>.component1(): A = this.first

internal operator fun <A, B> Pair<A, B>.component2(): B = this.second

internal fun <T : Any> mapCodec(builder: (RecordCodecBuilder.Instance<T>) -> App<RecordCodecBuilder.Mu<T>, T>): Codec<T> {
    return RecordCodecBuilder.mapCodec(builder).codec()
}

internal fun ByteArray.bitsMatch(test: ByteArray, bits: Int): Boolean {
    require(this.size == test.size) { "Byte arrays must be of the same size" }

    var bitsLeft = bits
    // bytes are in big-endian form.
    var i = 0
    while (bitsLeft >= 8 && i < this.size) {
        if (this[i] != test[i]) return false
        i++
        bitsLeft -= 8
    }
    if (bitsLeft > 0) {
        val mask = 0xff shl (8 - bitsLeft)
        if ((this[i].toInt() and 0xff and mask) != (test[i].toInt() and 0xff and mask)) {
            return false
        }
    }
    return true
}

internal operator fun ByteArray.compareTo(other: ByteArray): Int {
    return (this zip other).asSequence()
        .map { (a, b) -> a.compareTo(b) }
        .firstOrNull { it != 0 }
        ?: this.size.compareTo(other.size)
}
