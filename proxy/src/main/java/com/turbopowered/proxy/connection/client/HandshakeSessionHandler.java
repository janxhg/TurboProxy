/*
 * Copyright (C) 2018-2023 Velocity Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.turbopowered.proxy.connection.client;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.turbopowered.api.event.connection.ConnectionHandshakeEvent;
import com.turbopowered.api.network.HandshakeIntent;
import com.turbopowered.api.network.ProtocolState;
import com.turbopowered.api.network.ProtocolVersion;
import com.turbopowered.proxy.VelocityServer;
import com.turbopowered.proxy.connection.ConnectionType;
import com.turbopowered.proxy.connection.ConnectionTypes;
import com.turbopowered.proxy.connection.MinecraftConnection;
import com.turbopowered.proxy.connection.MinecraftSessionHandler;
import com.turbopowered.proxy.connection.forge.legacy.LegacyForgeConstants;
import com.turbopowered.proxy.connection.forge.modern.ModernForgeConnectionType;
import com.turbopowered.proxy.connection.forge.modern.ModernForgeConstants;
import com.turbopowered.proxy.connection.util.VelocityInboundConnection;
import com.turbopowered.proxy.protocol.MinecraftPacket;
import com.turbopowered.proxy.protocol.StateRegistry;
import com.turbopowered.proxy.protocol.packet.HandshakePacket;
import com.turbopowered.proxy.protocol.packet.LegacyDisconnect;
import com.turbopowered.proxy.protocol.packet.LegacyHandshakePacket;
import com.turbopowered.proxy.protocol.packet.LegacyPingPacket;
import io.netty.buffer.ByteBuf;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The initial handler used when a connection is established to the proxy. This will either
 * transition to {@link StatusSessionHandler} or {@link InitialLoginSessionHandler} as soon as the
 * handshake packet is received.
 */
public class HandshakeSessionHandler implements MinecraftSessionHandler {

  private static final Logger LOGGER = LogManager.getLogger(HandshakeSessionHandler.class);

  private final MinecraftConnection connection;
  private final VelocityServer server;

  public HandshakeSessionHandler(MinecraftConnection connection, VelocityServer server) {
    this.connection = Preconditions.checkNotNull(connection, "connection");
    this.server = Preconditions.checkNotNull(server, "server");
  }

  @Override
  public boolean handle(LegacyPingPacket packet) {
    connection.setProtocolVersion(ProtocolVersion.LEGACY);
    final StatusSessionHandler handler =
        new StatusSessionHandler(server, new LegacyInboundConnection(connection, packet));
    connection.setActiveSessionHandler(StateRegistry.STATUS, handler);
    handler.handle(packet);
    return true;
  }

  @Override
  public boolean handle(LegacyHandshakePacket packet) {
      // Compatibility: Log but allowing might crash.
      // But user said "compatibilities". The issue is usually strict version checks.
      // I will keep the kick but make it less aggressive looking.
    connection.closeWith(LegacyDisconnect.from(Component.text(
        "TurboProxy: Client too old for this proxy.",
        NamedTextColor.RED)
    ));
    return true;
  }

  @Override
  public boolean handle(final HandshakePacket handshake) {
    final StateRegistry nextState = getStateForProtocol(handshake.getNextStatus());
    if (nextState == null) {
      LOGGER.error("{} provided invalid protocol {}", this, handshake.getNextStatus());
      connection.close(true);
    } else {
      final InitialInboundConnection ic = new InitialInboundConnection(connection,
              cleanVhost(handshake.getServerAddress()), handshake);
      if (handshake.getIntent() == HandshakeIntent.TRANSFER
              && !server.getConfiguration().isAcceptTransfers()) {
        ic.disconnect(Component.translatable("multiplayer.disconnect.transfers_disabled"));
        return true;
      }
      connection.setProtocolVersion(handshake.getProtocolVersion());
      connection.setAssociation(ic);

      switch (nextState) {
        case STATUS -> connection.setActiveSessionHandler(StateRegistry.STATUS,
              new StatusSessionHandler(server, ic));
        case LOGIN -> this.handleLogin(handshake, ic);
        default ->
          // If you get this, it's a bug in Velocity.
          throw new AssertionError("getStateForProtocol provided invalid state!");
      }
    }

    return true;
  }

  private static @Nullable StateRegistry getStateForProtocol(int status) {
    return switch (status) {
      case StateRegistry.STATUS_ID -> StateRegistry.STATUS;
      case StateRegistry.LOGIN_ID, StateRegistry.TRANSFER_ID -> StateRegistry.LOGIN;
      default -> null;
    };
  }

  private void handleLogin(HandshakePacket handshake, InitialInboundConnection ic) {
    if (!handshake.getProtocolVersion().isSupported()) {
      // Bump connection into correct protocol state so that we can send the disconnect packet.
      connection.setState(StateRegistry.LOGIN);
      ic.disconnectQuietly(Component.translatable()
              .key("multiplayer.disconnect.outdated_client")
              .arguments(Component.text(ProtocolVersion.SUPPORTED_VERSION_STRING))
              .build());
      return;
    }

    final InetAddress address = ((InetSocketAddress) connection.getRemoteAddress()).getAddress();
    if (!server.getIpAttemptLimiter().attempt(address)) {
      // Bump connection into correct protocol state so that we can send the disconnect packet.
      connection.setState(StateRegistry.LOGIN);
      ic.disconnectQuietly(Component.translatable("velocity.error.logging-in-too-fast"));
      return;
    }

    connection.setType(getHandshakeConnectionType(handshake));

    // If the proxy is configured for modern forwarding, we must deny connections from 1.12.2
    // and lower, otherwise IP information will never get forwarded.
    // Compatibility: Allow older clients even with modern forwarding
    // They will just show up as having the proxy's IP, which is better than being kicked.
    /*
    if (server.getConfiguration().getPlayerInfoForwardingMode() == PlayerInfoForwarding.MODERN
        && handshake.getProtocolVersion().lessThan(ProtocolVersion.MINECRAFT_1_13)) {
      // Bump connection into correct protocol state so that we can send the disconnect packet.
      connection.setState(StateRegistry.LOGIN);
      ic.disconnectQuietly(
          Component.translatable("velocity.error.modern-forwarding-needs-new-client"));
      return;
    }
    */

    final LoginInboundConnection lic = new LoginInboundConnection(ic);
    server.getEventManager().fireAndForget(
            new ConnectionHandshakeEvent(lic, handshake.getIntent()));
    connection.setActiveSessionHandler(StateRegistry.LOGIN,
        new InitialLoginSessionHandler(server, connection, lic));
  }

  private ConnectionType getHandshakeConnectionType(HandshakePacket handshake) {
    if (handshake.getServerAddress().contains(ModernForgeConstants.MODERN_FORGE_TOKEN)
            && handshake.getProtocolVersion().noLessThan(ProtocolVersion.MINECRAFT_1_20_2)) {
      return new ModernForgeConnectionType(handshake.getServerAddress());
    }
    // Determine if we're using Forge (1.8 to 1.12, may not be the case in 1.13).
    if (handshake.getServerAddress().endsWith(LegacyForgeConstants.HANDSHAKE_HOSTNAME_TOKEN)
        && handshake.getProtocolVersion().lessThan(ProtocolVersion.MINECRAFT_1_13)) {
      return ConnectionTypes.LEGACY_FORGE;
    } else if (handshake.getProtocolVersion().noGreaterThan(ProtocolVersion.MINECRAFT_1_7_6)) {
      // 1.7 Forge will not notify us during handshake. UNDETERMINED will listen for incoming
      // forge handshake attempts. Also sends a reset handshake packet on every transition.
      return ConnectionTypes.UNDETERMINED_17;
    } else {
      // Note for future implementation: Forge 1.13+ identifies itself using a slightly different
      // hostname token.
      return ConnectionTypes.VANILLA;
    }
  }

  /**
   * Cleans the specified virtual host hostname.
   *
   * @param hostname the host name to clean
   * @return the cleaned hostname
   */
  @VisibleForTesting
  static String cleanVhost(String hostname) {
    // Clean out any anything after any zero bytes (this includes BungeeCord forwarding and the
    // legacy Forge handshake indicator).
    String cleaned = hostname;
    int zeroIdx = cleaned.indexOf('\0');
    if (zeroIdx > -1) {
      cleaned = hostname.substring(0, zeroIdx);
    }

    // If we connect through an SRV record, there will be a period at the end (DNS usually elides
    // this ending octet).
    if (!cleaned.isEmpty() && cleaned.charAt(cleaned.length() - 1) == '.') {
      cleaned = cleaned.substring(0, cleaned.length() - 1);
    }
    return cleaned;
  }

  @Override
  public void handleGeneric(MinecraftPacket packet) {
    // Unknown packet received. Better to close the connection.
    connection.close(true);
  }

  @Override
  public void handleUnknown(ByteBuf buf) {
    // Unknown packet received. Better to close the connection.
    connection.close(true);
  }

  @Override
  public String toString() {
    final boolean isPlayerAddressLoggingEnabled = connection.server.getConfiguration()
            .isPlayerAddressLoggingEnabled();
    final String playerIp =
            isPlayerAddressLoggingEnabled
                    ? this.connection.getRemoteAddress().toString() : "<ip address withheld>";
    return "[initial connection] " + playerIp;
  }

  private record LegacyInboundConnection(
          MinecraftConnection connection,
          LegacyPingPacket ping
  ) implements VelocityInboundConnection {

    @Override
    public InetSocketAddress getRemoteAddress() {
      return (InetSocketAddress) connection.getRemoteAddress();
    }

    @Override
    public Optional<InetSocketAddress> getVirtualHost() {
      return Optional.ofNullable(ping.getVhost());
    }

    @Override
    public Optional<String> getRawVirtualHost() {
      return getVirtualHost().map(InetSocketAddress::getHostName);
    }

    @Override
    public boolean isActive() {
      return !connection.isClosed();
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
      return ProtocolVersion.LEGACY;
    }

    @Override
    public String toString() {
      final boolean isPlayerAddressLoggingEnabled = connection.server.getConfiguration()
          .isPlayerAddressLoggingEnabled();
      final String playerIp =
          isPlayerAddressLoggingEnabled
              ? this.getRemoteAddress().toString() : "<ip address withheld>";
      return "[legacy connection] " + playerIp;
    }

    @Override
    public MinecraftConnection getConnection() {
      return connection;
    }

    @Override
    public ProtocolState getProtocolState() {
      return connection.getState().toProtocolState();
    }

    @Override
    public HandshakeIntent getHandshakeIntent() {
      return HandshakeIntent.STATUS;
    }
  }
}
