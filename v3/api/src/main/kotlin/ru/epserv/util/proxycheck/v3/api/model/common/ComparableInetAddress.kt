package ru.epserv.util.proxycheck.v3.api.model.common

import org.jetbrains.annotations.ApiStatus
import ru.epserv.util.proxycheck.v3.api.util.compareTo
import java.net.InetAddress

/**
 * A wrapper around [InetAddress] that implements [Comparable] based on the byte representation of the address.
 *
 * @property value the wrapped [InetAddress]
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
class ComparableInetAddress(val value: InetAddress) : Comparable<ComparableInetAddress> {
    /**
     * Creates a [ComparableInetAddress] from the given address bytes.
     *
     * @param bytes address bytes
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    constructor(bytes: ByteArray) : this(InetAddress.getByAddress(bytes))

    override fun compareTo(other: ComparableInetAddress): Int {
        return this.value.address.compareTo(other.value.address)
    }
}
