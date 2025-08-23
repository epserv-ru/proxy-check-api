package ru.epserv.util.proxycheck.v3.api.model.common

import ru.epserv.util.proxycheck.v3.api.util.compareTo
import java.net.InetAddress

class ComparableInetAddress(val value: InetAddress) : Comparable<ComparableInetAddress> {
    constructor(bytes: ByteArray) : this(InetAddress.getByAddress(bytes))

    override fun compareTo(other: ComparableInetAddress): Int {
        return this.value.address.compareTo(other.value.address)
    }
}
