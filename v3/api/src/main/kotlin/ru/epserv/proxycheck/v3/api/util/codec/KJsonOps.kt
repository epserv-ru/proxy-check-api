@file:OptIn(ExperimentalSerializationApi::class)

package ru.epserv.proxycheck.v3.api.util.codec

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import ru.epserv.proxycheck.v3.api.util.component1
import ru.epserv.proxycheck.v3.api.util.component2
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.UnaryOperator
import java.util.stream.Stream
import kotlin.streams.asStream

object KJsonOps : DynamicOps<JsonElement> {
    override fun empty(): JsonElement = JsonNull

    override fun <U : Any> convertTo(outOps: DynamicOps<U>, input: JsonElement): U {
        when (input) {
            is JsonObject -> return this.convertMap(outOps, input)
            is JsonArray -> return this.convertList(outOps, input)
            is JsonNull -> return outOps.empty()
            is JsonPrimitive -> {
                if (input.isString) return outOps.createString(input.content)

                input.booleanOrNull?.let { return outOps.createBoolean(it) }
                input.longOrNull?.let {
                    return when {
                        (it.toByte().toLong() == it) -> outOps.createByte(it.toByte())
                        (it.toShort().toLong() == it) -> outOps.createShort(it.toShort())
                        (it.toInt().toLong() == it) -> outOps.createInt(it.toInt())
                        else -> outOps.createLong(it)
                    }
                }
                input.doubleOrNull?.let {
                    return when {
                        (it.toFloat().toDouble() == it) -> outOps.createFloat(it.toFloat())
                        else -> outOps.createDouble(it)
                    }
                }

                throw kotlin.IllegalArgumentException("Unknown JsonPrimitive: $input")
            }
        }
    }

    override fun getNumberValue(input: JsonElement): DataResult<Number> {
        return (input as? JsonPrimitive)?.takeUnless { it.isString }?.doubleOrNull?.let(DataResult<Number>::success)
            ?: DataResult.error { "Not a number: $input" }
    }

    override fun createNumeric(i: Number): JsonElement = JsonPrimitive(i)

    override fun getBooleanValue(input: JsonElement): DataResult<Boolean> {
        return (input as? JsonPrimitive)?.takeUnless { it.isString }?.booleanOrNull?.let(DataResult<Boolean>::success)
            ?: DataResult.error { "Not a boolean: $input" }
    }

    override fun createBoolean(value: Boolean): JsonElement = JsonPrimitive(value)

    override fun getStringValue(input: JsonElement): DataResult<String> {
        return (input as? JsonPrimitive)?.takeIf { it.isString }?.content?.let(DataResult<String>::success)
            ?: DataResult.error { "Not a string: $input" }
    }

    override fun createString(value: String): JsonElement = JsonPrimitive(value)

    override fun mergeToList(list: JsonElement, value: JsonElement): DataResult<JsonElement> {
        if (list !is JsonArray && list !== this.empty()) {
            return DataResult.error({ "mergeToList called with not a list: $list" }, list)
        }

        return DataResult.success(buildJsonArray {
            if (list is JsonArray) addAll(list)
            add(value)
        })
    }

    override fun mergeToList(list: JsonElement, values: List<JsonElement>): DataResult<JsonElement> {
        if (list !is JsonArray && list !== this.empty()) {
            return DataResult.error({ "mergeToList called with not a list: $list" }, list)
        }

        return DataResult.success(buildJsonArray {
            if (list is JsonArray) addAll(list)
            addAll(values)
        })
    }

    override fun mergeToMap(map: JsonElement, key: JsonElement, value: JsonElement): DataResult<JsonElement> {
        if (map !is JsonObject && map !== this.empty()) {
            return DataResult.error({ "mergeToMap called with not a map: $map" }, map)
        }
        if (key !is JsonPrimitive || !key.isString) {
            return DataResult.error({ "key is not a string: $key" }, map)
        }

        return DataResult.success(buildJsonObject {
            if (map is JsonObject) map.forEach(::put)
            put(key.content, value)
        })
    }

    override fun mergeToMap(map: JsonElement, values: MapLike<JsonElement>): DataResult<JsonElement> {
        if (map !is JsonObject && map !== this.empty()) {
            return DataResult.error({ "mergeToMap called with not a map: $map" }, map)
        }

        val missed = mutableListOf<JsonElement>()

        val output = buildJsonObject {
            if (map is JsonObject) map.forEach(::put)

            values.entries().forEach { (key, value) ->
                if (key !is JsonPrimitive || !key.isString) {
                    missed.add(key)
                } else {
                    put(key.content, value)
                }
            }
        }

        if (missed.isNotEmpty()) return DataResult.error({ "some keys are not strings: $missed" }, output)

        return DataResult.success(output)
    }

    override fun getMapValues(input: JsonElement): DataResult<Stream<Pair<JsonElement, JsonElement?>>> {
        if (input !is JsonObject) return DataResult.error { "Not a JSON object: $input" }

        return DataResult.success(input.asSequence()
            .map { (key, value) -> Pair.of(JsonPrimitive(key) as JsonElement, value.takeUnless { it is JsonNull }) }
            .asStream())
    }

    override fun getMapEntries(input: JsonElement): DataResult<Consumer<BiConsumer<JsonElement, JsonElement?>>> {
        if (input !is JsonObject) return DataResult.error { "Not a JSON object: $input" }

        return DataResult.success(Consumer { consumer ->
            input.forEach { (key, value) ->
                consumer.accept(this.createString(key), value.takeUnless { it is JsonNull })
            }
        })
    }

    override fun getMap(input: JsonElement): DataResult<MapLike<JsonElement>> {
        if (input !is JsonObject) return DataResult.error { "Not a JSON object: $input" }

        return DataResult.success(object : MapLike<JsonElement> {
            override fun get(key: JsonElement): JsonElement? = this.get(key.jsonPrimitive.content)

            override fun get(key: String): JsonElement? = input[key]?.takeUnless { it is JsonNull }

            override fun entries(): Stream<Pair<JsonElement, JsonElement>> {
                return input.asSequence()
                    .map { (key, value) -> Pair.of(JsonPrimitive(key) as JsonElement, value) }
                    .asStream()
            }

            override fun toString(): String = "MapLike[$input]"
        })
    }

    override fun createMap(map: Stream<Pair<JsonElement, JsonElement>>): JsonElement {
        return buildJsonObject {
            map.forEach { (key, value) -> put(key.jsonPrimitive.content, value) }
        }
    }

    override fun getStream(input: JsonElement): DataResult<Stream<JsonElement?>> {
        if (input !is JsonArray) return DataResult.error { "Not a JSON array: $input" }

        return DataResult.success(input.asSequence().map { it.takeUnless { it is JsonNull } }.asStream())
    }

    override fun getList(input: JsonElement): DataResult<Consumer<Consumer<JsonElement?>>> {
        if (input !is JsonArray) return DataResult.error { "Not a JSON array: $input" }

        return DataResult.success(Consumer { consumer ->
            input.forEach { element ->
                consumer.accept(element.takeUnless { it is JsonNull })
            }
        })
    }

    override fun createList(input: Stream<JsonElement>): JsonElement = buildJsonArray { input.forEach(::add) }

    override fun remove(input: JsonElement, key: String): JsonElement {
        if (input !is JsonObject) return input

        return buildJsonObject {
            input.forEach { (k, v) ->
                if (k != key) put(k, v)
            }
        }
    }

    override fun listBuilder(): ListBuilder<JsonElement> = ArrayBuilder()

    private class ArrayBuilder : ListBuilder<JsonElement> {
        private var builder = DataResult.success(buildJsonArray {}, Lifecycle.stable())

        override fun ops(): DynamicOps<JsonElement> = KJsonOps

        override fun add(value: JsonElement): ListBuilder<JsonElement> {
            this.builder = this.builder.map {
                buildJsonArray {
                    addAll(it)
                    add(value)
                }
            }
            return this
        }

        override fun add(value: DataResult<JsonElement>): ListBuilder<JsonElement> {
            this.builder = this.builder.apply2stable({ builder, element ->
                buildJsonArray {
                    addAll(builder)
                    add(element)
                }
            }, value)
            return this
        }

        override fun withErrorsFrom(result: DataResult<*>): ListBuilder<JsonElement> {
            this.builder = this.builder.flatMap { builder -> result.map { builder } }
            return this
        }

        override fun mapError(onError: UnaryOperator<String>): ListBuilder<JsonElement> {
            this.builder = this.builder.mapError(onError)
            return this
        }

        override fun build(prefix: JsonElement): DataResult<JsonElement> {
            val result = this.builder.flatMap { builder ->
                if (prefix !is JsonArray && prefix !== this.ops().empty()) {
                    return@flatMap DataResult.error({ "Cannot append a list to not a list: $prefix" }, prefix)
                }

                DataResult.success(buildJsonArray {
                    if (prefix is JsonArray) addAll(prefix)
                    addAll(builder)
                }, Lifecycle.stable())
            }

            this.builder = DataResult.success(buildJsonArray {}, Lifecycle.stable())
            return result
        }
    }

    override fun mapBuilder(): RecordBuilder<JsonElement> = JsonRecordBuilder()

    private class JsonRecordBuilder : RecordBuilder.AbstractStringBuilder<JsonElement, JsonObject>(this@KJsonOps) {
        override fun initBuilder(): JsonObject = buildJsonObject {}

        override fun append(key: String, value: JsonElement, builder: JsonObject): JsonObject {
            return buildJsonObject {
                builder.forEach(::put)
                put(key, value)
            }
        }

        override fun build(builder: JsonObject, prefix: JsonElement?): DataResult<JsonElement> {
            if (prefix == null || prefix is JsonNull) return DataResult.success(builder)

            if (prefix !is JsonObject) return DataResult.error({ "mergeToMap called with not a map: $prefix" }, prefix)

            return DataResult.success(buildJsonObject {
                prefix.forEach(::put)
                builder.forEach(::put)
            })
        }
    }
}
