# Velocity Changelog

## v1.0.0 - LZ4 Compression Support

### Summary
Implemented a custom compression factory to use LZ4 for backend server connections, optimizing throughput and latency for the internal network.

### Technical Details
- **Core**: Added `Lz4VelocityCompressor` using `lz4-java` library (Direct ByteBuffers).
- **Injection**: Overloaded `MinecraftConnection.setCompressionThreshold` to accept custom compressor factories.
- **Session Handling**: Updated `LoginSessionHandler` to intercept compression packets and inject the LZ4 factory for backend connections.

### Installation & Config
- **Build**: `gradle shadowJar`
- **Config (`velocity.toml`)**:
  - `online-mode = false` (for this dev environment)
  - `player-info-forwarding-mode = "modern"`
  - `forwarding-secret-file` configured.
