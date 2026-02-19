/*
 * Copyright (c) 2026 ElectroPlay
 * SPDX-License-Identifier: MPL-2.0
 */

package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import org.jetbrains.annotations.ApiStatus

/**
 * Value for [Operator.anonymity] and [Operator.popularity] fields.
 *
 * @property id string representation of the rating
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
enum class OperatorRating(val id: String) {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
    ;

    companion object {
        private val values = entries.associateBy { it.id }

        @ApiStatus.Internal
        val CODEC: Codec<OperatorRating> = Codec.STRING.comapFlatMap(
            { value -> values[value]?.let { DataResult.success(it) } ?: DataResult.error { "Unknown operator rating: $value" } },
            { it.id },
        )
    }
}
