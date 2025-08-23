package ru.epserv.util.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult

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

        val CODEC: Codec<ResponseStatus> = Codec.STRING.comapFlatMap(
            { value -> index[value]?.let { DataResult.success(it) } ?: DataResult.error { "Unknown response status: $value" } },
            ResponseStatus::value,
        )
    }
}
