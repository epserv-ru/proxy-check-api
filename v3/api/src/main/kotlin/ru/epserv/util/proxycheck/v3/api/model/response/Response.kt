package ru.epserv.util.proxycheck.v3.api.model.response

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import ru.epserv.util.proxycheck.v3.api.util.mapCodec
import java.net.InetAddress

sealed interface Response {
    val status: ResponseStatus
    val isSuccessful: Boolean
        get() = this.status.isSuccessful

    fun successOrThrow(): Success = this as? Success ?: throw RuntimeException("Received an unsuccessful response with status ${this.status}")

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
            val CODEC = mapCodec { instance ->
                instance.group(
                    ResponseStatus.CODEC.fieldOf("status").forGetter(Success::status),
                    AddressResult.MULTIPLE_MAP_CODEC.forGetter(Success::results),
                    Codec.STRING.optionalFieldOf("message", null).forGetter(Success::message),
                    Codec.STRING.optionalFieldOf("node", null).forGetter(Success::node),
                ).apply(instance, ::Success)
            }
        }
    }

    data class Failure(
        override val status: ResponseStatus,
        val message: String? = null,
    ) : Response {
        init {
            require(!this.status.isSuccessful) { "Cannot create an unsuccessful response with a successful status: ${this.status}" }
            require(!this.status.hasMessage || this.message != null) { "Response status ${this.status} requires a message, but none was provided" }
        }

        companion object {
            val CODEC = mapCodec { instance ->
                instance.group(
                    ResponseStatus.CODEC.fieldOf("status").forGetter(Failure::status),
                    Codec.STRING.optionalFieldOf("message", null).forGetter(Failure::message),
                ).apply(instance, ::Failure)
            }
        }
    }

    companion object {
        val CODEC: Codec<Response> = Codec.either(Success.CODEC, Failure.CODEC).xmap(
            { either -> either.map({ it }, { it }) },
            { response -> if (response.isSuccessful) Either.left(response as Success) else Either.right(response as Failure) },
        )
    }
}
