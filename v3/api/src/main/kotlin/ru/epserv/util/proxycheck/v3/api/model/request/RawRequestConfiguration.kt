package ru.epserv.util.proxycheck.v3.api.model.request

import org.jetbrains.annotations.ApiStatus
import ru.epserv.util.proxycheck.v3.api.util.toHttpQueryString

/**
 * Request configuration in a format that is sent to the API endpoint.
 *
 * See [proxycheck.io/api](https://proxycheck.io/api/) for more info.
 *
 * @property key API key
 * @property ver API version override, defaults to the version in the customer dashboard
 * @property node whether to return the node that processed the request (`1` or `0`), defaults to `0`
 * @property p whether to pretty-print the JSON response (`1` or `0`), defaults to `1`
 * @property days maximum number of days since the IP was last seen as a proxy, defaults to `7`
 * @property tag custom tag to be included in the response, defaults to `null`
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
data class RawRequestConfiguration(
    val key: String?,
    val ver: String?,
    val node: Int?,
    val p: Int?,
    val days: Int?,
    val tag: String?,
) {
    /**
     * Converts this configuration to a map of query parameters.
     *
     * @return map of query parameters
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    @ApiStatus.Internal
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

    /**
     * Converts this configuration to an HTTP query string.
     *
     * @return HTTP query string
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    @ApiStatus.Internal
    fun toHttpQueryString(): String = this.toMap().toHttpQueryString()
}
