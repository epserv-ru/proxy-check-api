package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.datafixers.util.Either
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.model.request.RequestConfiguration
import ru.epserv.proxycheck.v3.api.util.*
import ru.epserv.proxycheck.v3.api.util.codec.Codecs.forNullableGetter
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
     * Successful response.
     *
     * @property status response status
     * @property results map of IP addresses to their results
     * @property message optional message (present if [ResponseStatus.hasMessage] is `true`)
     * @property node optional node identifier (present if [RequestConfiguration.returnNode] was set to `true`)
     * @property queryTime time the server took to process the query in milliseconds, excluding network RTT
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    data class Success(
        override val status: ResponseStatus,
        val results: Map<InetAddress, AddressResult>,
        val message: String?,
        val node: String?,
        val queryTime: Long?,
    ) : Response {
        init {
            require(this.status.isSuccessful) { "Cannot create a successful response with an unsuccessful status: ${this.status}" }
            require(!this.status.hasMessage || this.message != null) { "Response status ${this.status} requires a message, but none was provided" }
        }

        private constructor(
            status: ResponseStatus,
            results: Map<InetAddress, AddressResult>,
            message: Optional<String>,
            node: Optional<String>,
            queryTime: Optional<Long>,
        ) : this(
            status = status,
            results = results,
            message = message.getOrNull(),
            node = node.getOrNull(),
            queryTime = queryTime.getOrNull(),
        )

        companion object {
            @ApiStatus.Internal
            internal val METADATA_MAP_CODEC = mapCodec { instance ->
                instance.group(
                    ResponseStatus.CODEC.fieldOf("status").forGetter(Success::status),
                    Codec.STRING.optionalFieldOf("message").forNullableGetter(Success::message),
                    Codec.STRING.optionalFieldOf("node").forNullableGetter(Success::node),
                    Codec.LONG.optionalFieldOf("query_time").forNullableGetter(Success::queryTime),
                ).apply(instance) { status, message, node, queryTime ->
                    Success(
                        status = status,
                        results = emptyMap(),
                        message = message,
                        node = node,
                        queryTime = queryTime,
                    )
                }
            }

            @ApiStatus.Internal
            internal val METADATA_CODEC = METADATA_MAP_CODEC.toCodec()

            @ApiStatus.Internal
            internal val CODEC = SuccessCodec

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
                    val nonMetadataKeys = METADATA_MAP_CODEC.keys(ops).toList()

                    return ops.getMap(input)
                        .map { mapLike -> ops.createMap(mapLike.entries().filter { (key, _) -> key !in nonMetadataKeys }) }
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
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    data class Failure(
        override val status: ResponseStatus,
        val message: String? = null,
    ) : Response {
        init {
            require(!this.status.isSuccessful) { "Cannot create an unsuccessful response with a successful status: ${this.status}" }
            require(!this.status.hasMessage || this.message != null) { "Response status ${this.status} requires a message, but none was provided" }
        }

        private constructor(
            status: ResponseStatus,
            message: Optional<String>,
        ) : this(
            status = status,
            message = message.getOrNull(),
        )

        companion object {
            @ApiStatus.Internal
            internal val CODEC = buildMapCodec { instance ->
                instance.group(
                    ResponseStatus.CODEC.fieldOf("status").forGetter(Failure::status),
                    Codec.STRING.optionalFieldOf("message").forNullableGetter(Failure::message),
                ).apply(instance, ::Failure)
            }
        }
    }

    companion object {
        @ApiStatus.Internal
        val CODEC: Codec<Response> = Codec.either(Success.CODEC, Failure.CODEC).xmap(
            { either -> either.map({ it }, { it }) },
            { response -> if (response.isSuccessful) Either.left(response as Success) else Either.right(response as Failure) },
        )
    }
}
