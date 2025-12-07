# 🚀 TurboProxy Full Feature List & Changelog

Este documento detalla todas las modificaciones, mejoras y características agregadas a **TurboProxy** (Fork de Velocity) hasta la versión 1.2.0.

## 🛠️ Núcleo y Rendimiento (Core & Performance)

### ⚡ Motor de Red Optimizado
*   **Netty 4.1.107.Final**: Actualización del motor de red asíncrono a una versión más reciente para mejorar el throughput y reducir la latencia.
*   **Soporte IO_URING**: Integración nativa de `io_uring` para sistemas Linux, permitiendo una gestión de I/O de red mucho más eficiente que el estándar `epoll`.
*   **Compresión Nativa (LZ4)**: Implementación de librerías nativas para la compresión de paquetes (`Lz4VelocityCompressor`), reduciendo el uso de CPU durante la transferencia de datos.

### ☕ Plataforma Java
*   **Java 21 Nativo**: El proyecto ha sido migrado para compilar y ejecutarse sobre **JDK 21**, aprovechando las últimas optimizaciones de la JVM y el Garbage Collector (ZGC/G1 de nueva generación).

### 🔒 Seguridad Criptográfica
*   **RSA 2048-bit**: Se incrementó el tamaño de las claves de encriptación generadas por el servidor de 1024 a **2048 bits**, mejorando significativamente la seguridad de la sesión.

---

## 🛡️ Sistema Anti-Bot Nativo (The Sentinel Update)

TurboProxy incluye un sistema de protección de capa de aplicación (L7) integrado directamente en el núcleo, sin necesidad de plugins externos.

### 🚧 Filtrado de Conexiones
*   **AntiBotService**: Un nuevo servicio interno que intercepta conexiones antes del handshake de Minecraft.
*   **Challenge Mode (Modo Desafío)**:
    *   Mecanismo inteligente de "Greylist".
    *   La primera conexión de una IP desconocida es rechazada instantáneamente (Drop).
    *   Si la IP reintenta demasiado rápido (ataque agresivo), sigue bloqueada.
    *   Si reintenta en un intervalo humano (ej. 3-5 segundos), se le permite el paso y se añade a una whitelist temporal.
*   **Connection Throttling**: Limita el número máximo de conexiones simultáneas permitidas desde una misma dirección IP.

### 📋 Listas de Control
*   **Blacklist**: Bloqueo inmediato y persistente de IPs maliciosas detectadas.
*   **Whitelist**: Exención de chequeos para IPs de confianza.

---

## 📊 Observabilidad Avanzada (Advanced Observability)

Infraestructura de monitoreo profesional compatible con **Prometheus**.

### 📡 Endpoint HTTP Nativo
*   El proxy levanta un servidor HTTP ligero en un puerto dedicado (Default: `30069`).
*   Ruta: `/metrics`.
*   Formato: Prometheus Text Format (compatible con Grafana).

### 📈 Métricas en Tiempo Real
Sin necesidad de plugins, el proxy expone:
*   `proxy_players_online`: Cantidad de jugadores conectados en tiempo real.
*   `jvm_memory_heap_used`: Uso de memoria RAM del proceso Java (bytes).
*   `antibot_blocked_connections`: Contador acumulativo de conexiones rechazadas por el sistema Anti-Bot.

---

## ⚙️ Configuración y Experiencia de Usuario

### 🔧 `turbo.toml`
Una configuración simplificada y potente que reemplaza a la antigua configuración, con nuevas secciones dedicadas:
```toml
[antibot]
enabled = true
# Configuración granular del desafío
challengeEnabled = true
maxConnectionsPerIp = 5

[observability]
enabled = true
port = 30069
```

### 🏷️ Branding y CI/CD
*   **Marca TurboProxy**: Todos los mensajes de inicio, logs y headers han sido actualizados.
*   **Build System**: Correcciones en GitHub Actions para asegurar builds reproducibles en Windows y Linux.

---

## 📝 Resumen de Versiones

| Versión | Nombre Clave | Cambios Principales |
| :--- | :--- | :--- |
| **1.2.0** | *The Sentinel Update* | Native Anti-Bot, Advanced Observability, Metrics Endpoint. |
| **1.1.0** | *Performance Upgrade* | Java 21, Netty Updates, IO_URING, Compression fixes. |
| **1.0.0** | *Genesis* | Fork inicial de Velocity, limpieza de código legacy. |
