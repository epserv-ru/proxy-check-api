package ru.epserv.proxycheck.v3.api.util

import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress

internal object IpUtil {
    const val BYTES_V4 = 4
    const val BYTES_V6 = 16

    const val BITS_V4 = BYTES_V4 * 8
    const val BITS_V6 = BYTES_V6 * 8

    val ANY_V4: InetAddress = Inet4Address.getByAddress(ByteArray(BYTES_V4) { 0 })
    val ANY_V6: InetAddress = Inet6Address.getByAddress(ByteArray(BYTES_V6) { 0 })

    val InetAddress.sizeBytes: Int
        get() = when (this) {
            is Inet4Address -> BYTES_V4
            is Inet6Address -> BYTES_V6
        }

    val InetAddress.sizeBits: Int
        get() = when (this) {
            is Inet4Address -> BITS_V4
            is Inet6Address -> BITS_V6
        }

    fun InetAddress.isValidNetMaskBits(netMaskBits: Int): Boolean = netMaskBits in 0..this.sizeBits
}
