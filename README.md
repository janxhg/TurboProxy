# TurboProxy

**TurboProxy** is a next-generation Minecraft proxy, built for extreme performance, security, and modern compatibility. It is a high-performance fork of [Velocity](https://github.com/PaperMC/Velocity).

## 🚀 Key Features

*   **Java 21 Native**: Built for the latest JVM runtime, leveraging ZGC for sub-millisecond pauses.
*   **Netty 4.2 Engine**: Powered by the bleeding-edge Netty 4.2 networking stack.
*   **io_uring Support**: Native Linux integration using `io_uring` for massive connection throughput.
*   **Native Anti-Bot**: Integrated L4/L7 protection with Challenge Mode and Blacklisting.
*   **Observability**: Built-in Prometheus metrics endpoint for professional monitoring.
*   **Enhanced Security**: 2048-bit RSA encryption keys by default.

## 📥 Getting Started

1.  Download the latest release from the [Releases](https://github.com/janxhg/TurboProxy/releases) page.
2.  Run with Java 21+:
    ```bash
    java -Xms1G -Xmx1G -XX:+UseZGC -jar turbo-proxy-1.2.0-all.jar
    ```

## 🛠️ Building from Source

```bash
./gradlew :turbo-proxy:shadowJar
```

## ⚖️ License

TurboProxy is licensed under the GPLv3. See [LICENSE](LICENSE) for details.
Based on Velocity, Copyright (C) 2018-2023 Velocity Contributors.
