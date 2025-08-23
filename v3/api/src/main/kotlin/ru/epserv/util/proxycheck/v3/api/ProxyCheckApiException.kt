package ru.epserv.util.proxycheck.v3.api

import ru.epserv.util.proxycheck.v3.api.model.response.RawResponse

class ProxyCheckApiException : Exception {
    val rawResponse: RawResponse?

    constructor(rawResponse: RawResponse?, message: String) : super(message) {
        this.rawResponse = rawResponse
    }

    constructor(rawResponse: RawResponse?, message: String, cause: Throwable) : super(message, cause) {
        this.rawResponse = rawResponse
    }
}
