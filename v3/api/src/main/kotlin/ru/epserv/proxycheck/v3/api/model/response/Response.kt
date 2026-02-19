/*
 * Copyright (c) 2025-2026 ElectroPlay
 * SPDX-License-Identifier: MPL-2.0
 */

package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.datafixers.util.Either
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import kotlinx.serialization.json.JsonElement
import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.model.request.RequestConfiguration
import ru.epserv.proxycheck.v3.api.util.*
import ru.epserv.proxycheck.v3.api.util.codec.Codecs.forNullableGetter
import ru.epserv.proxycheck.v3.api.util.codec.KJsonOps
import java.net.InetAddress
import java.util.*
import kotlin.jvm.optionals.getOrNull

/**
 * proxycheck.io API response.
 *
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
sealed interface Response {
    /**
     * Response status.
     *
     * @since 1.0.0
     * @author metabrix
     */
    @get:ApiStatus.AvailableSince("1.0.0")
    val status: ResponseStatus

    /**
     * Response message (if present).
     *
     * @since 1.0.0
     * @author metabrix
     */
    @get:ApiStatus.AvailableSince("1.0.0")
    val message: String?

    /**
     * API version used for this request (if present).
     *
     * @since 1.0.0
     * @author metabrix
     */
    @get:ApiStatus.AvailableSince("1.0.0")
    val apiVersion: String?

    /**
     * Whether the response is successful ([ResponseStatus.isSuccessful]).
     *
     * @since 1.0.0
     * @author metabrix
     */
    @get:ApiStatus.AvailableSince("1.0.0")
    val isSuccessful: Boolean
        get() = this.status.isSuccessful

    /**
     * Returns the successful response or throws if the response was not successful.
     *
     * @return the successful response
     * @throws RuntimeException if the response was not successful
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    fun successOrThrow(): Success = this as? Success ?: throw RuntimeException("Received an unsuccessful response with status ${this.status}")

    /**
     * Encodes the response to [JsonElement].
     *
     * @return a JSON value representing this response
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    fun toJson(): JsonElement = CODEC.encodeStart(KJsonOps, this).orThrow

    /**
     * Successful response.
     *
     * @property status response status
     * @property message optional message (present if [ResponseStatus.hasMessage] is `true`)
     * @property apiVersion API version used for this request (if present)
     * @property results map of IP addresses to their results
     * @property node optional node identifier (present if [RequestConfiguration.returnNode] was set to `true`)
     * @property queryTime time the server took to process the query in milliseconds, excluding network RTT
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    data class Success(
        override val status: ResponseStatus,
        override val message: String?,
        override val apiVersion: String?,
        val results: Map<InetAddress, AddressResult>,
        val node: String?,
        val queryTime: Long?,
    ) : Response {
        init {
            require(this.status.isSuccessful) { "Cannot create a successful response with an unsuccessful status: ${this.status.value}" }
            require(!this.status.hasMessage || this.message != null) { "Response status '${this.status.value}' requires a message, but none was provided" }
        }

        private constructor(
            status: ResponseStatus,
            message: Optional<String>,
            apiVersion: Optional<String>,
            results: Map<InetAddress, AddressResult>,
            node: Optional<String>,
            queryTime: Optional<Long>,
        ) : this(
            status = status,
            message = message.getOrNull(),
            apiVersion = apiVersion.getOrNull(),
            results = results,
            node = node.getOrNull(),
            queryTime = queryTime.getOrNull(),
        )

        companion object {
            @ApiStatus.Internal
            val METADATA_MAP_CODEC = mapCodec { instance ->
                instance.group(
                    ResponseStatus.SUCCESSFUL_CODEC.fieldOf("status").forGetter(Success::status),
                    Codec.STRING.optionalFieldOf("message").forNullableGetter(Success::message),
                    Codec.STRING.optionalFieldOf("node").forNullableGetter(Success::node),
                    Codec.LONG.optionalFieldOf("query_time").forNullableGetter(Success::queryTime),
                    Codec.STRING.optionalFieldOf("version").forNullableGetter(Success::apiVersion),
                ).apply(instance) { status, message, node, queryTime, apiVersion ->
                    Success(
                        status = status,
                        message = message,
                        apiVersion = apiVersion,
                        results = emptyMap(),
                        node = node,
                        queryTime = queryTime,
                    )
                }
            }

            @ApiStatus.Internal
            val METADATA_CODEC = METADATA_MAP_CODEC.toCodec()

            @ApiStatus.Internal
            val CODEC: Codec<Success> = SuccessCodec

            internal object SuccessCodec : Codec<Success> {
                override fun <T : Any> encode(
                    input: Success,
                    ops: DynamicOps<T>,
                    prefix: T,
                ): DataResult<T> {
                    val metadataResult = METADATA_CODEC.encode(input, ops, prefix)
                    return AddressResult.IP_STRING_TO_RESULT_CODEC.encode(input.results, ops, metadataResult.orThrow)
                }

                override fun <T : Any> decode(
                    ops: DynamicOps<T>,
                    input: T,
                ): DataResult<Pair<Success, T>> {
                    val metadataResult = METADATA_CODEC.decode(ops, input)
                    val metadataKeys = METADATA_MAP_CODEC.keys(ops).toList()

                    return ops.getMap(input)
                        .map { mapLike -> ops.createMap(mapLike.entries().filter { (key, _) -> key !in metadataKeys }) }
                        .flatMap { remainingEntries -> AddressResult.IP_STRING_TO_RESULT_CODEC.decode(ops, remainingEntries) }
                        .map { (results, _) -> results }
                        .flatMap { results -> metadataResult.map { (metadata, _) -> metadata.copy(results = results) } }
                        .map { metadata -> Pair.of(metadata, input) }
                }
            }
        }
    }

    /**
     * Unsuccessful response.
     *
     * @property status response status
     * @property message optional message (present if [ResponseStatus.hasMessage] is `true`)
     * @property apiVersion API version used for this request (if present)
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    data class Failure(
        override val status: ResponseStatus,
        override val message: String? = null,
        override val apiVersion: String? = null,
    ) : Response {
        init {
            require(!this.status.isSuccessful) { "Cannot create an unsuccessful response with a successful status: ${this.status}" }
            require(!this.status.hasMessage || this.message != null) { "Response status ${this.status} requires a message, but none was provided" }
        }

        private constructor(
            status: ResponseStatus,
            message: Optional<String>,
            apiVersion: Optional<String>,
        ) : this(
            status = status,
            message = message.getOrNull(),
            apiVersion = apiVersion.getOrNull(),
        )

        companion object {
            @ApiStatus.Internal
            val CODEC = buildMapCodec { instance ->
                instance.group(
                    ResponseStatus.NON_SUCCESSFUL_CODEC.fieldOf("status").forGetter(Failure::status),
                    Codec.STRING.optionalFieldOf("message").forNullableGetter(Failure::message),
                    Codec.STRING.optionalFieldOf("version").forNullableGetter(Failure::apiVersion),
                ).apply(instance, ::Failure)
            }
        }
    }

    companion object {
        @get:ApiStatus.Internal
        val CODEC: Codec<Response> by lazy {
            Codec.either(Success.CODEC, Failure.CODEC).xmap(
                { either -> either.map({ it }, { it }) },
                { response -> if (response.isSuccessful) Either.left(response as Success) else Either.right(response as Failure) },
            )
        }
    }
}
