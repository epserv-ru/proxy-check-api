package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import org.jetbrains.annotations.ApiStatus

/**
 * Response status.
 *
 * @property value string representation of the status
 * @property isSuccessful whether the status indicates a successful response,
 *           meaning the response contains results
 * @property hasMessage whether the status includes a message, i.e., a warning or an error explanation
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
enum class ResponseStatus(
    val value: String,
    val isSuccessful: Boolean,
    val hasMessage: Boolean,
) {
    OK(value = "ok", isSuccessful = true, hasMessage = false),
    WARNING(value = "warning", isSuccessful = true, hasMessage = true),
    DENIED(value = "denied", isSuccessful = false, hasMessage = true),
    ERROR(value = "error", isSuccessful = false, hasMessage = true),
    ;

    companion object {
        private val index = entries.associateBy { it.value }

        @ApiStatus.Internal
        internal val CODEC: Codec<ResponseStatus> = Codec.STRING.comapFlatMap(
            { value -> index[value]?.let { DataResult.success(it) } ?: DataResult.error { "Unknown response status: $value" } },
            ResponseStatus::value,
        )
    }
}
