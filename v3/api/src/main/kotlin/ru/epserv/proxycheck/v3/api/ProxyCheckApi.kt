package ru.epserv.proxycheck.v3.api

import kotlinx.coroutines.future.await
import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.model.request.RequestConfiguration
import ru.epserv.proxycheck.v3.api.model.response.Response
import ru.epserv.proxycheck.v3.api.util.VersionInfo
import java.net.InetAddress
import java.util.concurrent.CompletableFuture

/**
 * API for `proxycheck.io/v3`.
 *
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
interface ProxyCheckApi : AutoCloseable {
    /**
     * API implementation version info.
     *
     * @since 1.0.0
     * @author metabrix
     */
    @get:ApiStatus.AvailableSince("1.0.0")
    val versionInfo: VersionInfo

    /**
     * Fetches the information on the given IP addresses and blocks until the response is received.
     *
     * @param addresses IP addresses to check
     * @param configure request configuration action
     * @return the response
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    fun check(
        addresses: Sequence<InetAddress>,
        configure: RequestConfiguration.() -> Unit = {},
    ): Response = this.checkAsync(addresses, configure).join()

    /**
     * Fetches the information on the given IP addresses and blocks until the response is received.
     *
     * @param addresses IP addresses to check
     * @param configure request configuration action
     * @return the response
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    fun check(
        addresses: Iterable<InetAddress>,
        configure: RequestConfiguration.() -> Unit = {},
    ): Response = this.check(addresses.asSequence(), configure)

    /**
     * Fetches the information on the given IP addresses and blocks until the response is received.
     *
     * @param addresses IP addresses to check
     * @param configure request configuration action
     * @return the response
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    fun check(
        vararg addresses: InetAddress,
        configure: RequestConfiguration.() -> Unit = {},
    ): Response = this.check(addresses.asSequence(), configure)

    /**
     * Fetches the information on the given IP addresses asynchronously.
     *
     * @param addresses IP addresses to check
     * @param configure request configuration action
     * @return a future that will be completed with the response
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    fun checkAsync(
        addresses: Sequence<InetAddress>,
        configure: RequestConfiguration.() -> Unit = {},
    ): CompletableFuture<Response>

    /**
     * Fetches the information on the given IP addresses asynchronously.
     *
     * @param addresses IP addresses to check
     * @param configure request configuration action
     * @return a future that will be completed with the response
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    fun checkAsync(
        addresses: Iterable<InetAddress>,
        configure: RequestConfiguration.() -> Unit = {},
    ): CompletableFuture<Response> = this.checkAsync(addresses.asSequence(), configure)

    /**
     * Fetches the information on the given IP addresses asynchronously.
     *
     * @param addresses IP addresses to check
     * @param configure request configuration action
     * @return a future that will be completed with the response
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    fun checkAsync(
        vararg addresses: InetAddress,
        configure: RequestConfiguration.() -> Unit = {},
    ): CompletableFuture<Response> = this.checkAsync(addresses.asSequence(), configure)

    /**
     * Fetches the information on the given IP addresses and suspends the coroutine until the response is received.
     *
     * @param addresses IP addresses to check
     * @param configure request configuration action
     * @return the response
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    suspend fun checkSuspend(
        addresses: Sequence<InetAddress>,
        configure: RequestConfiguration.() -> Unit = {},
    ): Response = this.checkAsync(addresses, configure).await()

    /**
     * Fetches the information on the given IP addresses and suspends the coroutine until the response is received.
     *
     * @param addresses IP addresses to check
     * @param configure request configuration action
     * @return the response
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    suspend fun checkSuspend(
        addresses: Iterable<InetAddress>,
        configure: RequestConfiguration.() -> Unit = {},
    ): Response = this.checkSuspend(addresses.asSequence(), configure)

    /**
     * Fetches the information on the given IP addresses and suspends the coroutine until the response is received.
     *
     * @param addresses IP addresses to check
     * @param configure request configuration action
     * @return the response
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    suspend fun checkSuspend(
        vararg addresses: InetAddress,
        configure: RequestConfiguration.() -> Unit = {},
    ): Response = this.checkSuspend(addresses.asSequence(), configure)
}
