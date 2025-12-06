# TurboProxy 🚀

[![Build Status](https://img.shields.io/github/actions/workflow/status/janxhg/TurboProxy/gradle.yml)](https://github.com/janxhg/TurboProxy)

**TurboProxy** is a high-performance Minecraft server proxy based on [Velocity](https://velocitypowered.com). It is designed to work seamlessly with **TurboMC**, providing enhanced features like native LZ4 compression and optimized connection handling.

## Key Features

*   **⚡ Native LZ4 Compression**: Optimized specifically for **TurboMC**, reducing CPU usage and latency.
*   **🛡️ Enhanced Security**: Improved handshake validation and bot mitigation.
*   **🚅 High Scalability**: Built on Velocity's asynchronous architecture to handle thousands of concurrent players.

## Building

TurboProxy is built with [Gradle](https://gradle.org).

```bash
./gradlew build
```

The output JAR will be located in `proxy/build/libs/`.

## Running

1.  Download the latest `TurboProxy` JAR.
2.  Run it with Java 21+:
    ```bash
    java -Xms1G -Xmx1G -jar turboproxy.jar
    ```

## License
TurboProxy is licensed under the GPLv3 license.
