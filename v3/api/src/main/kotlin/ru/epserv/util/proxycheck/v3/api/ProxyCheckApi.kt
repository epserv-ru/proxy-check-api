package ru.epserv.util.proxycheck.v3.api

import kotlinx.coroutines.future.await
import ru.epserv.util.proxycheck.v3.api.model.request.RequestConfiguration
import ru.epserv.util.proxycheck.v3.api.model.response.Response
import ru.epserv.util.proxycheck.v3.api.util.VersionInfo
import java.net.InetAddress
import java.util.concurrent.CompletableFuture

interface ProxyCheckApi : AutoCloseable {
    val versionInfo: VersionInfo

    fun check(
        addresses: Sequence<InetAddress>,
        configure: RequestConfiguration.() -> Unit,
    ): Response = this.checkAsync(addresses, configure).join()

    fun check(
        addresses: Iterable<InetAddress>,
        configure: RequestConfiguration.() -> Unit,
    ): Response = this.check(addresses.asSequence(), configure)

    fun check(
        vararg addresses: InetAddress,
        configure: RequestConfiguration.() -> Unit,
    ): Response = this.check(addresses.asSequence(), configure)

    fun checkAsync(
        addresses: Sequence<InetAddress>,
        configure: RequestConfiguration.() -> Unit,
    ): CompletableFuture<Response>

    fun checkAsync(
        addresses: Iterable<InetAddress>,
        configure: RequestConfiguration.() -> Unit,
    ): CompletableFuture<Response> = this.checkAsync(addresses.asSequence(), configure)

    fun checkAsync(
        vararg addresses: InetAddress,
        configure: RequestConfiguration.() -> Unit,
    ): CompletableFuture<Response> = this.checkAsync(addresses.asSequence(), configure)

    suspend fun checkSuspend(
        addresses: Sequence<InetAddress>,
        configure: RequestConfiguration.() -> Unit,
    ): Response = this.checkAsync(addresses, configure).await()

    suspend fun checkSuspend(
        addresses: Iterable<InetAddress>,
        configure: RequestConfiguration.() -> Unit,
    ): Response = this.checkSuspend(addresses.asSequence(), configure)

    suspend fun checkSuspend(
        vararg addresses: InetAddress,
        configure: RequestConfiguration.() -> Unit,
    ): Response = this.checkSuspend(addresses.asSequence(), configure)
}
