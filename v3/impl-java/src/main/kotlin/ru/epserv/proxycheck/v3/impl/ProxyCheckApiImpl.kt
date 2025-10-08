package ru.epserv.proxycheck.v3.impl

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import ru.epserv.proxycheck.v3.api.ProxyCheckApi
import ru.epserv.proxycheck.v3.api.ProxyCheckApiException
import ru.epserv.proxycheck.v3.api.model.request.RequestConfiguration
import ru.epserv.proxycheck.v3.api.model.response.RawResponse
import ru.epserv.proxycheck.v3.api.model.response.Response
import ru.epserv.proxycheck.v3.api.util.VersionInfo
import ru.epserv.proxycheck.v3.api.util.codec.KJsonOps
import ru.epserv.proxycheck.v3.api.util.urlEncode
import java.net.InetAddress
import java.net.ProxySelector
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture
import java.util.jar.JarInputStream
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class ProxyCheckApiImpl(
    val apiKey: String?,
    val apiEndpoint: URI = URI.create("https://proxycheck.io/v3/"),
    val connectTimeout: Duration = 10.seconds,
    val readTimeout: Duration = 10.seconds,
    val shutdownTimeout: Duration = 1.minutes,
    val proxySelector: ProxySelector = ProxySelector.getDefault(),
) : ProxyCheckApi {
    private val httpClient = HttpClient.newBuilder().apply {
        connectTimeout(connectTimeout.toJavaDuration())
        proxy(proxySelector)
    }.build()

    override val versionInfo: VersionInfo

    init {
        val manifest = JarInputStream(this.javaClass.protectionDomain.codeSource.location.openStream()).use { it.manifest }
        this.versionInfo = VersionInfoImpl(manifest)
    }

    override fun checkAsync(
        addresses: Sequence<InetAddress>,
        configure: RequestConfiguration.() -> Unit,
    ): CompletableFuture<Response> {
        val concatenatedAddresses = addresses.map { it.hostAddress }.joinToString(",").urlEncode()
        val postBody = "ips=$concatenatedAddresses"

        val requestConfiguration = RequestConfiguration()
        requestConfiguration.configure()

        val rawRequestConfiguration = requestConfiguration.toRaw(this.apiKey)
        val queryString = rawRequestConfiguration.toHttpQueryString().ifEmpty { null }

        val uri = if (queryString == null) this.apiEndpoint else this.apiEndpoint.resolve("?$queryString")

        val responseFuture = this.httpClient.sendAsync(
            HttpRequest.newBuilder(uri).apply {
                POST(HttpRequest.BodyPublishers.ofString(postBody))

                timeout(readTimeout.toJavaDuration())
                header("User-Agent", versionInfo.httpUserAgent)
                header("Content-Type", "application/x-www-form-urlencoded")
            }.build(),
            HttpResponse.BodyHandlers.ofString(Charsets.UTF_8),
        )

        return responseFuture
            .exceptionally { throwable ->
                throw ProxyCheckApiException(
                    rawResponse = null,
                    message = "Failed to fetch: ${uri.toMaskedString()}",
                    cause = throwable,
                )
            }
            .thenApply { httpResponse ->
                val rawResponse = RawResponse(
                    statusCode = httpResponse.statusCode(),
                    responseBody = httpResponse.body(),
                )

                if (rawResponse.statusCode !in 200..299 && rawResponse.statusCode != 400) {
                    throw ProxyCheckApiException(
                        rawResponse = rawResponse,
                        message = "Received a non-2xx status code (${rawResponse.statusCode}): ${uri.toMaskedString()}",
                    )
                }

                val responseJson = try {
                    Json.decodeFromString<JsonObject>(rawResponse.responseBody)
                } catch (cause: Exception) {
                    throw ProxyCheckApiException(
                        rawResponse = rawResponse,
                        message = "Failed to parse response body as a JSON object (status code ${rawResponse.statusCode}}): ${uri.toMaskedString()}",
                        cause = cause,
                    )
                }

                val response = try {
                    Response.CODEC.decode(KJsonOps, responseJson).orThrow.first
                } catch (cause: Exception) {
                    throw ProxyCheckApiException(
                        rawResponse = rawResponse,
                        message = "Failed to decode response: ${uri.toMaskedString()}",
                        cause = cause,
                    )
                }

                return@thenApply response
            }
    }

    override fun close() {
        this.httpClient.shutdown()
        if (this.httpClient.awaitTermination(this.shutdownTimeout.toJavaDuration())) return

        this.httpClient.shutdownNow()
    }

    private fun URI.toMaskedString(): String {
        var maskedUri = this@toMaskedString.toString()
        this@ProxyCheckApiImpl.apiKey?.let { maskedUri = maskedUri.replace(it, "<redacted>") }
        return maskedUri
    }
}
