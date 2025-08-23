package ru.epserv.proxycheck.v3.api.model.response

import org.jetbrains.annotations.ApiStatus

/**
 * Raw HTTP API response.
 *
 * @property statusCode HTTP status code
 * @property responseBody response body
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
data class RawResponse(
    val statusCode: Int,
    val responseBody: String,
)
