package ru.epserv.util.proxycheck.v3.api.model.request

import ru.epserv.util.proxycheck.v3.api.util.toHttpQueryString

data class RawRequestConfiguration(
    val key: String?,
    val ver: String?,
    val node: Int?,
    val p: Int?,
    val days: Int?,
    val tag: String?,
) {
    fun toMap(): Map<String, String> {
        return listOf(
            "key" to key,
            "ver" to ver,
            "node" to node,
            "p" to p,
            "days" to days,
            "tag" to tag,
        ).mapNotNull { (key, value) -> value?.let { key to it.toString() } }.toMap()
    }

    fun toHttpQueryString(): String = this.toMap().toHttpQueryString()
}
