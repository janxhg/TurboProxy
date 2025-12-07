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

package com.turbopowered.proxy.network;

import static com.turbopowered.proxy.network.Connections.FRAME_DECODER;
import static com.turbopowered.proxy.network.Connections.FRAME_ENCODER;
import static com.turbopowered.proxy.network.Connections.LEGACY_PING_DECODER;
import static com.turbopowered.proxy.network.Connections.LEGACY_PING_ENCODER;
import static com.turbopowered.proxy.network.Connections.MINECRAFT_DECODER;
import static com.turbopowered.proxy.network.Connections.MINECRAFT_ENCODER;
import static com.turbopowered.proxy.network.Connections.READ_TIMEOUT;

import com.turbopowered.proxy.VelocityServer;
import com.turbopowered.proxy.connection.MinecraftConnection;
import com.turbopowered.proxy.connection.client.HandshakeSessionHandler;
import com.turbopowered.proxy.protocol.ProtocolUtils;
import com.turbopowered.proxy.protocol.StateRegistry;
import com.turbopowered.proxy.protocol.netty.LegacyPingDecoder;
import com.turbopowered.proxy.protocol.netty.LegacyPingEncoder;
import com.turbopowered.proxy.protocol.netty.MinecraftDecoder;
import com.turbopowered.proxy.protocol.netty.MinecraftEncoder;
import com.turbopowered.proxy.protocol.netty.MinecraftVarintFrameDecoder;
import com.turbopowered.proxy.protocol.netty.MinecraftVarintLengthEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.TimeUnit;

/**
 * Server channel initializer.
 */
@SuppressWarnings("WeakerAccess")
public class ServerChannelInitializer extends ChannelInitializer<Channel> {

  private final VelocityServer server;

  public ServerChannelInitializer(final VelocityServer server) {
    this.server = server;
  }

  @Override
  protected void initChannel(final Channel ch) {
    ch.pipeline()
        .addLast(LEGACY_PING_DECODER, new LegacyPingDecoder())
        .addLast(FRAME_DECODER, new MinecraftVarintFrameDecoder(ProtocolUtils.Direction.SERVERBOUND))
        .addLast(READ_TIMEOUT,
            new ReadTimeoutHandler(this.server.getConfiguration().getReadTimeout(),
                TimeUnit.MILLISECONDS))
        .addLast(LEGACY_PING_ENCODER, LegacyPingEncoder.INSTANCE)
        .addLast(FRAME_ENCODER, MinecraftVarintLengthEncoder.INSTANCE)
        .addLast(MINECRAFT_DECODER, new MinecraftDecoder(ProtocolUtils.Direction.SERVERBOUND))
        .addLast(MINECRAFT_ENCODER, new MinecraftEncoder(ProtocolUtils.Direction.CLIENTBOUND));

    final MinecraftConnection connection = new MinecraftConnection(ch, this.server);
    connection.setActiveSessionHandler(StateRegistry.HANDSHAKE,
        new HandshakeSessionHandler(connection, this.server));
    ch.pipeline().addLast(Connections.HANDLER, connection);

    if (this.server.getConfiguration().isProxyProtocol()) {
      ch.pipeline().addFirst(new HAProxyMessageDecoder());
    }
  }
}
