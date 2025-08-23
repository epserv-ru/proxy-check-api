package ru.epserv.proxycheck.v3.api.model.common

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.util.IpUtil
import ru.epserv.proxycheck.v3.api.util.IpUtil.isValidNetMaskBits
import ru.epserv.proxycheck.v3.api.util.IpUtil.sizeBits
import ru.epserv.proxycheck.v3.api.util.bitsMatch
import ru.epserv.proxycheck.v3.api.util.codec.Codecs
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * An IP-address in [CIDR notation](https://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing).
 *
 * @property address the address
 * @property netMaskBits number of bits in the mask
 * @since 1.0.0
 * @author metabrix
 */
data class CidrIpRange(
    val address: InetAddress,
    val netMaskBits: Int,
) : ClosedRange<ComparableInetAddress> {
    constructor(address: InetAddress) : this(address, address.sizeBits)

    init {
        require(address.isValidNetMaskBits(netMaskBits)) { "Invalid netMaskBits: ${address.hostAddress}/$netMaskBits" }
    }

    private val addressBytes by lazy { address.address }

    /**
     * First address of the range (inclusive).
     *
     * @since 1.0.0
     * @author metabrix
     */
    override val start: ComparableInetAddress by lazy {
        val bytes = addressBytes.copyOf()

        if (netMaskBits % 8 == 0) {
            for (i in (netMaskBits / 8) until bytes.size) {
                bytes[i] = 0
            }
        } else {
            val fullBytes = netMaskBits / 8 // 2 for 21
            val prefixByteBits = netMaskBits % 8 // 5 for 21
            bytes[fullBytes] = bytes[fullBytes] and (0xff shl (8 - prefixByteBits)).toByte()
            for (i in (netMaskBits / 8 + 1) until bytes.size) {
                bytes[i] = 0
            }
        }

        return@lazy ComparableInetAddress(bytes)
    }

    /**
     * Last address of the range (inclusive).
     *
     * @since 1.0.0
     * @author metabrix
     */
    override val endInclusive: ComparableInetAddress by lazy {
        val bytes = start.value.address

        if (netMaskBits % 8 == 0) {
            for (i in (netMaskBits / 8) until bytes.size) {
                bytes[i] = 0xff.toByte()
            }
        } else {
            val fullBytes = netMaskBits / 8 // 2 for 21
            val prefixByteBits = netMaskBits % 8 // 5 for 21
            bytes[fullBytes] = bytes[fullBytes] or (0xff shr prefixByteBits).toByte()
            for (i in (netMaskBits / 8 + 1) until bytes.size) {
                bytes[i] = 0xff.toByte()
            }
        }

        return@lazy ComparableInetAddress(bytes)
    }

    /**
     * Checks whether this IP range contains the specified IP address.
     *
     * @param address the IP address to check
     * @return `true` if this range contains the specified address, otherwise `false`
     * @since 1.0.0
     * @author metabrix
     */
    operator fun contains(address: InetAddress): Boolean = when (address) {
        is Inet4Address -> this.address is Inet4Address && addressBytes.bitsMatch(address.address, netMaskBits)
        is Inet6Address -> this.address is Inet6Address && addressBytes.bitsMatch(address.address, netMaskBits)
            && (this.address.scopeId == 0 || this.address.scopeId == address.scopeId)
    }

    override fun toString(): String = "${address.hostAddress}/$netMaskBits"

    companion object {
        /**
         * All IPv4 addresses (`0.0.0.0/0`).
         *
         * @since 1.0.0
         * @author metabrix
         */
        @ApiStatus.AvailableSince("1.0.0")
        val ANY_V4 = CidrIpRange(IpUtil.ANY_V4, 0)

        /**
         * All IPv6 addresses (`::/0`).
         *
         * @since 1.0.0
         * @author metabrix
         */
        @ApiStatus.AvailableSince("1.0.0")
        val ANY_V6 = CidrIpRange(IpUtil.ANY_V6, 0)

        /**
         * A codec for [CidrIpRange] that encodes/decodes the full CIDR notation that includes
         * both the address and the netmask bits (e.g., `198.51.100.0/24` or `2001:db8::/32`).
         *
         * @since 1.0.0
         * @author metabrix
         */
        @ApiStatus.AvailableSince("1.0.0")
        val STRING_CODEC_FULL: Codec<CidrIpRange> = Codec.STRING.comapFlatMap(
            { string ->
                val parts = string.split("/")

                if (parts.size != 2) {
                    return@comapFlatMap DataResult.error {
                        "Invalid CIDR notation (expected to have 2 parts separated by a slash, but got ${parts.size}: $string"
                    }
                }

                val (addressString, netMaskBitsString) = parts

                val address = try {
                    InetAddress.getByName(addressString)
                } catch (e: Exception) {
                    return@comapFlatMap DataResult.error { "Invalid address $addressString: ${e.message}" }
                }

                val netMaskBits = try {
                    netMaskBitsString.toInt()
                } catch (e: Exception) {
                    return@comapFlatMap DataResult.error { "Invalid net mask bits $netMaskBitsString: ${e.message}" }
                }

                if (!address.isValidNetMaskBits(netMaskBits)) {
                    return@comapFlatMap DataResult.error { "Invalid net mask bits $netMaskBits for address $addressString" }
                }

                return@comapFlatMap DataResult.success(CidrIpRange(address, netMaskBits))
            },
            CidrIpRange::toString,
        )

        /**
         * A codec for [CidrIpRange] that accepts either a plain IP address (e.g., `198.51.100.84`
         * or `2001:db8::1`) that maps to a single-address CIDR range, or a full CIDR notation that
         * includes both the address and the netmask bits (e.g., `198.51.100.0/24` or `2001:db8::/32`).
         *
         * @since 1.0.0
         * @author metabrix
         */
        @ApiStatus.AvailableSince("1.0.0")
        val STRING_CODEC: Codec<CidrIpRange> = Codec.either(Codecs.INET_ADDRESS_STRING, STRING_CODEC_FULL).xmap(
            { either -> either.map({ address -> CidrIpRange(address) }, { range -> range }) },
            { range -> Either.right(range) },
        )
    }
}
