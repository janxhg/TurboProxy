# 🚀 TurboProxy Full Feature List & Changelog

This document details all modifications, improvements, and features added to **TurboProxy** (Fork of Velocity) up to version 1.2.0.

## 🛠️ Core & Performance

### ⚡ Optimized Network Engine
*   **Netty 4.1.107.Final**: Updated the asynchronous network engine to a newer version to improve throughput and reduce latency.
*   **IO_URING Support**: Native integration of `io_uring` for Linux systems, allowing for significantly more efficient network I/O compared to standard `epoll`.
*   **Native Compression (LZ4)**: Implementation of native libraries for packet compression (`Lz4VelocityCompressor`), reducing CPU usage during data transfer.

### ☕ Java Platform
*   **Native Java 21**: The project has been migrated to compile and run on **JDK 21**, leveraging the latest JVM optimizations and the Garbage Collector (ZGC/G1 next-gen).

### 🔒 Cryptographic Security
*   **RSA 2048-bit**: Increased the size of server-generated encryption keys from 1024 to **2048 bits**, significantly improving session security.



## 🛡️ Native Anti-Bot System (The Sentinel Update)

TurboProxy includes an application-layer protection system (L7) integrated directly into the core, without the need for external plugins.

### 🚧 Connection Filtering
*   **AntiBotService**: A new internal service that intercepts connections before the Minecraft handshake.
*   **Challenge Mode**:
    *   Intelligent "Greylist" mechanism.
    *   The first connection from an unknown IP is rejected instantly (Drop).
    *   If the IP retries too quickly (aggressive attack), it remains blocked.
    *   If it retries within a human interval (e.g., 3-5 seconds), it is allowed through and added to a temporary whitelist.
*   **Connection Throttling**: Limits the maximum number of simultaneous connections allowed from a single IP address.

### 📋 Control Lists
*   **Blacklist**: Immediate and persistent blocking of detected malicious IPs.
*   **Whitelist**: Exemption from checks for trusted IPs.



## 📊 Advanced Observability

Professional monitoring infrastructure compatible with **Prometheus**.

### 📡 Native HTTP Endpoint
*   The proxy starts a lightweight HTTP server on a dedicated port (Default: `30069`).
*   Route: `/metrics`.
*   Format: Prometheus Text Format (compatible with Grafana).

### 📈 Real-Time Metrics
Without external plugins, the proxy exposes:
*   `proxy_players_online`: Number of players connected in real-time.
*   `jvm_memory_heap_used`: RAM usage of the Java process (bytes).
*   `antibot_blocked_connections`: Cumulative counter of connections rejected by the Anti-Bot system.



## ⚙️ Configuration & User Experience

### 🔧 `turbo.toml`
A simplified and powerful configuration that replaces the old configuration, with new dedicated sections:
```toml
[antibot]
enabled = true
# Granular challenge configuration
challengeEnabled = true
maxConnectionsPerIp = 5

[observability]
enabled = true
port = 30069
```

### 🏷️ Branding & CI/CD
*   **TurboProxy Brand**: All startup messages, logs, and headers have been updated.
*   **Build System**: Fixes in GitHub Actions to ensure reproducible builds on Windows and Linux.



## 📝 Version Summary

| Version | Codename | Main Changes |
| :--- | :--- | :--- |
| **1.2.0** | *The Sentinel Update* | Native Anti-Bot, Advanced Observability, Metrics Endpoint. |
| **1.1.0** | *Performance Upgrade* | Java 21, Netty Updates, IO_URING, Compression fixes. |
| **1.0.0** | *Genesis* | Initial fork of Velocity, legacy code cleanup. |
