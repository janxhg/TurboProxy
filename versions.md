# TurboProxy Version History

## 1.2.0 (2025-12-07)
**"The Sentinel Update"**

### 🛡️ Native Anti-Bot System
*   **Connection Filtering**: Intelligent rate-limiting and connection tracking per IP.
*   **Blacklist/Whitelist**: Hot-reloadable lists to instantly block or allow IPs.
*   **Challenge Mode**: Native "Greylist" mechanism to verify legitimate non-bot connections (inspired by FlameCord/Aegis).

### 📊 Advanced Observability
*   **Prometheus Endpoint**: Built-in HTTP metrics server (`/metrics`) compatible with Prometheus/Grafana.
*   **Metrics**:
    *   `proxy_players_online`: Real-time player count.
    *   `jvm_memory_heap_used`: Memory usage monitoring.
    *   `antibot_blocked_connections`: Counter for total attacks mitigated.

### 🛠 Fixes & Improvements
*   **CI/CD**: Fixed GitHub Actions build for cross-platform compatibility.
*   **Testing**: Added Unit Tests for Anti-Bot logic and Metrics service.

## 1.1.0 (2025-12-06)
**"The Performance Update"**

### 🌟 New Features
*   **Netty 4.2 Core**: Upgraded networking engine to Netty 4.2.7.Final.
*   **io_uring**: Enabled native Linux `io_uring` transport for superior throughput.
*   **Java 21**: Project now targets Java 21 LTS, enabling ZGC support.
*   **Security**: Default RSA key size increased to 2048-bit.
*   **Branding**: Full rebranding from Velocity to TurboProxy (packages `com.turbopowered.*`).

### 🛠 Fixes & Improvements
*   Protocol support for Minecraft 1.21.x.
*   Fixed dependency resolution for native transports.
*   Cleaned up build logic and configuration files (`turbo.toml`).


## 1.0.0 (2025-12-07)
**"The Rebirth"**

### 🚀 Initial Release
*   **Total Rebrand**: Forked from Velocity 3.4.0-SNAPSHOT.
*   **Package Rename**: Migrated all code to `com.turbopowered`.
*   **Configuration**: Introduced `turbo.toml` configuration system.
*   **Build System**: Complete overhaul of Gradle build logic for TurboProxy.
*   **Fixes**: Solved initial dependency and shading issues.
*   **Compression**: Added native LZ4 support for faster packet compression.

---
*Based on Velocity 3.4.0-SNAPSHOT*
