package ru.epserv.util.proxycheck.v3.api.model.request

class RequestConfiguration {
    var prettyPrint: Boolean = false
    var maxDays: Int = 7
    var returnNode: Boolean = false
    var tag: String? = null

    fun toRaw(key: String?, ver: String? = null) = RawRequestConfiguration(
        key = key,
        ver = ver,
        node = if (this.returnNode) 1 else 0,
        p = if (this.prettyPrint) 1 else 0,
        days = this.maxDays,
        tag = this.tag,
    )
}
