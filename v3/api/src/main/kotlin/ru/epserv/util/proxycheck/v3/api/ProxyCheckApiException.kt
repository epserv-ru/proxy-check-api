package ru.epserv.util.proxycheck.v3.api

import org.jetbrains.annotations.ApiStatus
import ru.epserv.util.proxycheck.v3.api.model.response.RawResponse

/**
 * Exception thrown when an error occurs while interacting with the proxycheck.io API before the response is fully processed.
 *
 * @property rawResponse the raw HTTP response received from the API, if available
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
class ProxyCheckApiException : Exception {
    val rawResponse: RawResponse?

    constructor(rawResponse: RawResponse?, message: String) : super(message) {
        this.rawResponse = rawResponse
    }

    constructor(rawResponse: RawResponse?, message: String, cause: Throwable) : super(message, cause) {
        this.rawResponse = rawResponse
    }
}
