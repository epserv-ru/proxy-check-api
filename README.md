# proxy-check-api

[![Build and test](https://github.com/epserv-ru/proxy-check-api/actions/workflows/build.yml/badge.svg)](https://github.com/epserv-ru/proxy-check-api/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/ru.epserv.proxycheck.v3/proxy-check-v3-impl-java)](https://central.sonatype.com/artifact/ru.epserv.proxycheck.v3/proxy-check-v3-impl-java)
[![License: MPL-2.0](https://img.shields.io/badge/License-MPL--2.0-blue.svg)](LICENSE)

Kotlin/JVM bindings for the [proxycheck.io](https://proxycheck.io/) v3 API.

The project is split into two published modules:

- `proxy-check-v3-api`: public API interfaces, request models, response models, and utility types.
- `proxy-check-v3-impl-java`: Java `HttpClient` based implementation of the v3 API.

## Supported Features

- Java/Kotlin library for the proxycheck.io v3 API.
- Full IP address checks, including multi-address requests.
- Full response model decoding for network, location, detection, detection history, attack history, operator, node, query time, status, and message fields.
- Blocking, `CompletableFuture`, and coroutine-friendly request methods.
- Request options for pretty printing, maximum detection age, response node, and custom tags.
- Configurable API endpoint, proxy selector, connection/read/shutdown timeouts, and API version override.
- API implementation metadata, including build/version information and HTTP user agent.

## Requirements

- Java 21 or newer
- Kotlin/JVM project
- A proxycheck.io API key is recommended for production use

## Installation

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("ru.epserv.proxycheck.v3:proxy-check-v3-impl-java:1.0.0")
}
```

Use `proxy-check-v3-api` directly only when you need the interfaces and model types without the bundled Java HTTP client implementation.

```kotlin
dependencies {
    implementation("ru.epserv.proxycheck.v3:proxy-check-v3-api:1.0.0")
}
```

## Quick Start

```kotlin
import ru.epserv.proxycheck.v3.impl.ProxyCheckApiImpl
import java.net.InetAddress

val api = ProxyCheckApiImpl.build(
    apiKey = System.getenv("PROXYCHECK_API_KEY"),
)

try {
    val response = api.check(InetAddress.getByName("8.8.8.8")) {
        maxDays = 30
        tag = "example"
    }

    val result = response
        .successOrThrow()
        .results
        .getValue(InetAddress.getByName("8.8.8.8"))

    println("Proxy: ${result.detections.proxy}")
    println("VPN: ${result.detections.vpn}")
    println("Risk: ${result.detections.risk}")
    println("Network type: ${result.network.type}")
} finally {
    api.close()
}
```

`ProxyCheckApi` is `AutoCloseable`. Long-lived applications should close the client during shutdown. For one-off client operations, you can also use Kotlin's `use` helper:

```kotlin
ProxyCheckApiImpl.build(apiKey = System.getenv("PROXYCHECK_API_KEY")).use { api ->
    val response = api.check(InetAddress.getByName("8.8.8.8"))
    println(response.status.value)
}
```

## API Styles

The `ProxyCheckApi` interface supports blocking, Java async, and coroutine-friendly access:

```kotlin
val blockingResponse = api.check(InetAddress.getByName("8.8.8.8"))

val futureResponse = api.checkAsync(InetAddress.getByName("8.8.8.8"))

val suspendResponse = api.checkSuspend(InetAddress.getByName("8.8.8.8"))
```

Each method accepts a single `InetAddress`, varargs, an `Iterable<InetAddress>`, or a `Sequence<InetAddress>`.

## Request Options

Configure request query parameters with the trailing configuration block:

```kotlin
val response = api.check(InetAddress.getByName("8.8.8.8")) {
    prettyPrint = false
    maxDays = 7
    returnNode = true
    tag = "login-check"
}
```

Available options:

- `prettyPrint`: request pretty-printed JSON from proxycheck.io.
- `maxDays`: maximum number of days since the IP was last seen as a proxy.
- `returnNode`: include the proxycheck.io node that processed the request.
- `tag`: custom tag included in the response.

## Implementation Configuration

`ProxyCheckApiImpl.build` also accepts implementation-level configuration:

```kotlin
import kotlin.time.Duration.Companion.seconds

val api = ProxyCheckApiImpl.build(apiKey = System.getenv("PROXYCHECK_API_KEY")) {
    timeout {
        connectTimeout = 5.seconds
        readTimeout = 10.seconds
    }
}
```

The default endpoint is `https://proxycheck.io/v3/`. The implementation also supports custom endpoints, a custom `ProxySelector`, shutdown timeout tuning, and an unsupported API version override. The version override is intended for experiments only; this library build targets `11-February-2026`.

## Development

Build and run checks:

```bash
./gradlew build
```

Run formatting checks:

```bash
./gradlew spotlessCheck
```

Assemble JAR artifacts:

```bash
./gradlew assemble
```

CI runs:

```bash
./gradlew build --no-daemon --console=plain --stacktrace
```

The root `assemble` task copies module JARs into `build/libs`.

## License

This project is licensed under the [Mozilla Public License 2.0](LICENSE).
