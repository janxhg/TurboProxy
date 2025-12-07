# TurboProxy Roadmap

Strategic plan for the evolution of TurboProxy into a high-performance, secure, and developer-friendly platform.

## 1. 🧪 Plugin API Verification ("Hello Turbo")
**Goal**: Ensure the new `com.turbopowered` namespace is developer-ready.
- [ ] Create a reference plugin ("TurboTest").
- [ ] Verify event listeners work (PlayerJoin, ProxyInitialize).
- [ ] Confirm command registration works.
- [ ] Document migration guide for Velocity plugin developers.

## 2. 🔥 Stress Testing (High Performance Validation)
**Goal**: Validate `io_uring` and ZGC benefits under load.
- [ ] Setup a local bot swarm (using `bots_mc` tool).
- [ ] Benchmark memory usage vs Player Count.
- [ ] Tune ZGC flags for optimal latency.
- [ ] Verify Netty 4.2 stability.

## 3. 🛡️ Native Anti-Bot System
**Goal**: Native Layer 4/7 protection against attacks.
- [x] **Connection Throttling**: Intelligent rate limiting per IP/Subnet in Netty pipeline.
- [x] **Early filtering**: Drop malicious connections before SSL handshake if possible.
- [x] **Challenge Mode**: (Planned) Simple state-check before allowing login.
- [x] **Configuration**: Hot-reloadable anti-bot settings in `turbo.toml`.

## 4. 📊 Advanced Observability
**Goal**: Professional monitoring.
- [ ] Expose Netty EventLoop metrics (lag, task queue size).
- [ ] Prometheus endpoint (optional plugin or internal module).
- [ ] Packet compression stats (LZ4 efficiency).
