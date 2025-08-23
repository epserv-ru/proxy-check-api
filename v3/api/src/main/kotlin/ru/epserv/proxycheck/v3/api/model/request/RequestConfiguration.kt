package ru.epserv.proxycheck.v3.api.model.request

import org.jetbrains.annotations.ApiStatus

/**
 * Mutable request configuration. Used as a receiver in request configuration methods.
 *
 * @property prettyPrint whether to pretty-print the response JSON, defaults to `false`
 * @property maxDays maximum number of days since the IP was last seen as a proxy, defaults to `7`
 * @property returnNode whether to include the node that processed the request in the response, defaults to `false`
 * @property tag custom tag to be included in the response, defaults to `null`
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
class RequestConfiguration {
    var prettyPrint: Boolean = false
    var maxDays: Int = 7
    var returnNode: Boolean = false
    var tag: String? = null

    /**
     * Converts this configuration to a [RawRequestConfiguration] that can be sent to the API endpoint.
     *
     * @param key the API key (may be null, though not recommended)
     * @param ver API version override, defaults to the version in the customer dashboard
     * @return the raw request configuration
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    fun toRaw(key: String?, ver: String? = null) = RawRequestConfiguration(
        key = key,
        ver = ver,
        node = if (this.returnNode) 1 else 0,
        p = if (this.prettyPrint) 1 else 0,
        days = this.maxDays,
        tag = this.tag,
    )
}
