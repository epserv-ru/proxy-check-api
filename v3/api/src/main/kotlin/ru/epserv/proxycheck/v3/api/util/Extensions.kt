package ru.epserv.proxycheck.v3.api.util

import com.mojang.datafixers.kinds.App
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.JavaOps
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.OptionalFieldCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.net.URLEncoder
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.typeOf
import kotlin.streams.asSequence

fun String.urlEncode(): String = URLEncoder.encode(this, Charsets.UTF_8)

internal fun Map<String, String>.toHttpQueryString(): String {
    return this
        .map { (key, value) -> key.urlEncode() to value.urlEncode() }
        .joinToString("&") { (key, value) -> "$key=$value" }
}

internal operator fun <A, B> Pair<A, B>.component1(): A = this.first

internal operator fun <A, B> Pair<A, B>.component2(): B = this.second

internal fun <T : Any> mapCodec(builder: (RecordCodecBuilder.Instance<T>) -> App<RecordCodecBuilder.Mu<T>, T>): MapCodec<T> {
    return RecordCodecBuilder.mapCodec(builder)
}

internal fun <T : Any> buildMapCodec(builder: (RecordCodecBuilder.Instance<T>) -> App<RecordCodecBuilder.Mu<T>, T>): Codec<T> {
    return mapCodec(builder).codec()
}

internal fun <T : Any> MapCodec<T>.toCodec(): Codec<T> = this.codec()

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


private val optionalFieldCodecNameProperty = fieldNameProperty<OptionalFieldCodec<*>>()

internal val OptionalFieldCodec<*>.name: String
    get() = optionalFieldCodecNameProperty.get(this)

internal val MapCodec<*>.name: String?
    get() {
        if (this is OptionalFieldCodec<*>) return this.name
        return this.keys(JavaOps.INSTANCE).asSequence().distinct().filterIsInstance<String>().singleOrNull()
    }

@Suppress("UNCHECKED_CAST")
private inline fun <reified T : Any> fieldNameProperty(): KProperty1<T, String> {
    return T::class.declaredMemberProperties
        .single { it.name == "name" && it.returnType.classifier!! == typeOf<String>().classifier!! }
        .apply { isAccessible = true }
        as KProperty1<T, String>
}
