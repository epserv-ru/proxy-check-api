/*
 * Copyright (c) 2025-2026 ElectroPlay
 * SPDX-License-Identifier: MPL-2.0
 */

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

    override fun toString(): String = this.value

    companion object {
        private val index = entries.associateBy { it.value }

        @ApiStatus.Internal
        val CODEC: Codec<ResponseStatus> = Codec.STRING.comapFlatMap(
            { value -> index[value]?.let { DataResult.success(it) } ?: DataResult.error { "Unknown response status: $value" } },
            ResponseStatus::value,
        )

        @ApiStatus.Internal
        val SUCCESSFUL_CODEC: Codec<ResponseStatus> = CODEC.flatXmap(::asDataResultSuccessful, ::asDataResultSuccessful)

        @ApiStatus.Internal
        val NON_SUCCESSFUL_CODEC: Codec<ResponseStatus> = CODEC.flatXmap(::asDataResultNonSuccessful, ::asDataResultNonSuccessful)

        private fun asDataResultSuccessful(status: ResponseStatus): DataResult<ResponseStatus> {
            return if (status.isSuccessful) DataResult.success(status) else DataResult.error { "Unsuccessful response status: ${status.value}" }
        }

        private fun asDataResultNonSuccessful(status: ResponseStatus): DataResult<ResponseStatus> {
            return if (!status.isSuccessful) DataResult.success(status) else DataResult.error { "Successful response status: ${status.value}" }
        }
    }
}
