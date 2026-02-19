/*
 * Copyright (c) 2025-2026 ElectroPlay
 * SPDX-License-Identifier: MPL-2.0
 */

package ru.epserv.proxycheck.v3.api.util.codec

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.net.InetAddress
import java.util.*
import kotlin.time.Instant

internal object Codecs {
    val INET_ADDRESS_STRING: Codec<InetAddress> = Codec.STRING.comapFlatMap(this::decodeInetAddress, InetAddress::getHostAddress)
    val ASN_STRING: Codec<Int> = Codec.STRING.comapFlatMap(
        { string ->
            try {
                DataResult.success(string.removePrefix("AS").toInt())
            } catch (e: Exception) {
                DataResult.error { "Failed to decode ASN from string '$string': ${e.message}" }
            }
        },
        { asn -> "AS$asn" },
    )

    val INSTANT_ISO_8601: Codec<Instant> = Codec.STRING.comapFlatMap(
        { iso8601String ->
            try {
                DataResult.success(Instant.parse(iso8601String))
            } catch (e: Exception) {
                DataResult.error { "Failed to decode Instant from ISO 8601 string '$iso8601String': ${e.message}" }
            }
        },
        { instant -> instant.toString() },
    )

    fun <O : Any, A : Any> MapCodec<Optional<A>>.forNullableGetter(getter: (O) -> A?): RecordCodecBuilder<O, Optional<A>> {
        return this.forGetter { obj -> Optional.ofNullable(getter(obj)) }
    }

    fun <A : Any> Codec<A>.setOf(): Codec<Set<A>> = this.listOf().xmap({ it.toSet() }, { it.toList() })

    fun <K : Any, V : Any> Codec<K>.associatedWith(valueCodec: Codec<V>): MapCodec<Map<K, V>> {
        return MapCodec.assumeMapUnsafe(Codec.unboundedMap(this, valueCodec))
    }

    fun decodeInetAddress(string: String): DataResult<InetAddress> {
        return this.decodeInet4Address(string).orElseGet { errorV4 ->
            this.decodeInet6Address(string).orElseGet { errorV6 ->
                DataResult.error { "Failed to decode IP address '$string': [v4: ${errorV4.message()}], [v6: ${errorV6.message()}]" }
            }
        }
    }

    private fun decodeInet4Address(string: String): DataResult<InetAddress> {
        val parts = string.split(".")
        if (parts.size != 4) {
            return DataResult.error { "Invalid IPv4 address '$string': expected 4 parts, but got ${parts.size}" }
        }

        try {
            val bytes = parts
                .map { it.toUByte() }
                .map { it.toByte() }
                .toByteArray()
            return DataResult.success(InetAddress.getByAddress(bytes))
        } catch (e: Exception) {
            return DataResult.error { "Invalid IPv4 address '$string': ${e.message}" }
        }
    }

    private fun decodeInet6Address(string: String): DataResult<InetAddress> {
        val compressedParts = string.split("::")
        val bytesResult = when (compressedParts.size) {
            1 -> this.decodeInet6AddressBytesFull(compressedParts[0])
            2 -> this.decodeInet6AddressBytesCompressed(string, compressedParts[0], compressedParts[1])
            else -> DataResult.error { "Invalid IPv6 address '$string': unknown format" }
        }

        return bytesResult.flatMap { bytes ->
            try {
                DataResult.success(InetAddress.getByAddress(bytes))
            } catch (e: Exception) {
                DataResult.error { "Invalid IPv6 address '$string': ${e.message}" }
            }
        }
    }

    private fun decodeInet6AddressBytesFull(string: String): DataResult<ByteArray> {
        val parts = string.split(":")
        if (parts.size != 8) {
            return DataResult.error { "Invalid IPv6 address '$string': expected 8 parts, but got ${parts.size}" }
        }

        return try {
            DataResult.success(this.decodeInet6AddressBytes(parts))
        } catch (e: Exception) {
            DataResult.error { "Invalid IPv6 address '$string': ${e.message}" }
        }
    }

    private fun decodeInet6AddressBytesCompressed(string: String, start: String, end: String): DataResult<ByteArray> {
        val partsStart = start.split(":")
        val partsEnd = end.split(":")
        if (partsStart.size + partsEnd.size > 8) {
            return DataResult.error { "Invalid IPv6 address '$string': too many parts after compression" }
        }

        return try {
            val startBytes = this.decodeInet6AddressBytes(partsStart)
            val endBytes = this.decodeInet6AddressBytes(partsEnd)

            val fullBytes = startBytes.copyOf(16)
            endBytes.copyInto(fullBytes, destinationOffset = 16 - endBytes.size)
            DataResult.success(fullBytes)
        } catch (e: Exception) {
            DataResult.error { "Invalid IPv6 address '$string': ${e.message}" }
        }
    }

    private fun decodeInet6AddressBytes(parts: List<String>): ByteArray {
        return parts
            .flatMap { it.padStart(4, '0').chunked(2) }
            .map { it.toUByte(16) }
            .map { it.toByte() }
            .toByteArray()
    }

    private fun <T : Any> DataResult<T>.orElseGet(other: (DataResult.Error<T>) -> DataResult<T>): DataResult<T> = this.error().map(other).orElse(this)
}
