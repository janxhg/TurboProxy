package com.velocitypowered.proxy.network.handler;

import com.velocitypowered.proxy.antibot.AntiBotService;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;

/**
 * Netty handler that enforces Anti-Bot rules at the very start of the pipeline.
 * If rejected, the channel is closed immediately before any resource-intensive processing.
 */
public class AntiBotHandler extends ChannelInboundHandlerAdapter {

  private final AntiBotService service;

  public AntiBotHandler(AntiBotService service) {
    this.service = service;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    if (ctx.channel().remoteAddress() instanceof InetSocketAddress) {
      InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
      if (!service.allowConnection(remoteAddress.getAddress())) {
        // Close immediately without firing further events
        ctx.close();
        return;
      }
    }
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    if (ctx.channel().remoteAddress() instanceof InetSocketAddress) {
      InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
      service.onConnectionClosed(remoteAddress.getAddress());
    }
    super.channelInactive(ctx);
  }
}
