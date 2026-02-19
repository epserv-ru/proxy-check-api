/*
 * Copyright (c) 2026 ElectroPlay
 * SPDX-License-Identifier: MPL-2.0
 */

package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.util.buildMapCodec
import ru.epserv.proxycheck.v3.api.util.codec.Codecs
import kotlin.time.Instant

/**
 * Detection history for the IP address.
 *
 * @property delisted whether the IP address was delisted and is no longer considered
 *           positive detection
 * @property delistDateTime the date and time when the IP address was delisted
 *           (or will be delisted, if in the future)
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
data class DetectionHistory(
    val delisted: Boolean,
    val delistDateTime: Instant,
) {
    companion object {
        @ApiStatus.Internal
        val CODEC = buildMapCodec { instance ->
            instance.group(
                Codec.BOOL.fieldOf("delisted").forGetter(DetectionHistory::delisted),
                Codecs.INSTANT_ISO_8601.fieldOf("delist_datetime").forGetter(DetectionHistory::delistDateTime),
            ).apply(instance, ::DetectionHistory)
        }
    }
}
