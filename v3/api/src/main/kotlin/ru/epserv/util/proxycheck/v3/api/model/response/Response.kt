package ru.epserv.util.proxycheck.v3.api.model.response

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import ru.epserv.util.proxycheck.v3.api.model.request.RequestConfiguration
import ru.epserv.util.proxycheck.v3.api.util.mapCodec
import java.net.InetAddress

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
     * @since 1.0.0
     * @author metabrix
     */
    @ApiStatus.AvailableSince("1.0.0")
    data class Success(
        override val status: ResponseStatus,
        val results: Map<InetAddress, AddressResult>,
        val message: String?,
        val node: String?,
    ) : Response {
        init {
            require(this.status.isSuccessful) { "Cannot create a successful response with an unsuccessful status: ${this.status}" }
            require(!this.status.hasMessage || this.message != null) { "Response status ${this.status} requires a message, but none was provided" }
        }

        companion object {
            @ApiStatus.Internal
            internal val CODEC = mapCodec { instance ->
                instance.group(
                    ResponseStatus.CODEC.fieldOf("status").forGetter(Success::status),
                    AddressResult.MULTIPLE_MAP_CODEC.forGetter(Success::results),
                    Codec.STRING.optionalFieldOf("message", null).forGetter(Success::message),
                    Codec.STRING.optionalFieldOf("node", null).forGetter(Success::node),
                ).apply(instance, ::Success)
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

        companion object {
            @ApiStatus.Internal
            internal val CODEC = mapCodec { instance ->
                instance.group(
                    ResponseStatus.CODEC.fieldOf("status").forGetter(Failure::status),
                    Codec.STRING.optionalFieldOf("message", null).forGetter(Failure::message),
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
