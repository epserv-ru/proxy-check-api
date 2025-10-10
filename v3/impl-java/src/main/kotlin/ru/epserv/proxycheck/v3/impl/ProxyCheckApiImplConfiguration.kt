package ru.epserv.proxycheck.v3.impl

import org.jetbrains.annotations.ApiStatus
import java.net.ProxySelector
import java.net.URI
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Configuration for [ProxyCheckApiImpl].
 *
 * @property connection connection establishment configuration
 * @property timeout configuration for various timeouts
 * @property unsupported configuration settings that may result in untested and/or unstable behavior
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
data class ProxyCheckApiImplConfiguration(
    var connection: ConnectionConfiguration = ConnectionConfiguration(),
    var timeout: TimeoutConfiguration = TimeoutConfiguration(),
    var unsupported: UnsupportedConfiguration = UnsupportedConfiguration(),
) {
    /**
     * Updates the connection establishment configuration.
     *
     * @param configure configuration action
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    fun connection(configure: ConnectionConfiguration.() -> Unit) {
        this.connection.apply(configure)
    }

    /**
     * Updates the timeout configuration.
     *
     * @param block configuration action
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    fun timeout(block: TimeoutConfiguration.() -> Unit) {
        this.timeout.apply(block)
    }

    /**
     * Updates the unsupported configuration.
     *
     * @param block configuration action
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    fun unsupported(block: UnsupportedConfiguration.() -> Unit) {
        this.unsupported.apply(block)
    }

    /**
     * Connection establishment configuration.
     *
     * @property apiEndpoint API endpoint URI, defaults to `https://proxycheck.io/v3/`
     * @property proxySelector proxy selector, defaults to the system default
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    data class ConnectionConfiguration(
        @ApiStatus.AvailableSince("1.0.0")
        var apiEndpoint: URI = URI.create("https://proxycheck.io/v3/"),
        @ApiStatus.AvailableSince("1.0.0")
        var proxySelector: ProxySelector = ProxySelector.getDefault(),
    )

    /**
     * Configuration for various timeouts.
     *
     * @property connectTimeout connection establishment timeout, defaults to 10 seconds
     * @property readTimeout read timeout, defaults to 10 seconds
     * @property shutdownTimeout timeout for graceful shutdown, defaults to 1 minute
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    data class TimeoutConfiguration(
        @ApiStatus.AvailableSince("1.0.0")
        var connectTimeout: Duration = 10.seconds,
        @ApiStatus.AvailableSince("1.0.0")
        var readTimeout: Duration = 10.seconds,
        @ApiStatus.AvailableSince("1.0.0")
        var shutdownTimeout: Duration = 1.minutes,
    )

    /**
     * Configuration settings that may result in untested and/or unstable behavior.
     *
     * @property apiVersion API version override, defaults to the version that this library build
     *           is targeting. While older or newer versions may work just fine, the library was not
     *           tested against them and provides no stability guarantees.
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    data class UnsupportedConfiguration(
        @ApiStatus.AvailableSince("1.0.0")
        var apiVersion: String = "12-August-2025",
    )
}
