package com.velocitypowered.proxy.network.handler;

import com.velocitypowered.proxy.util.metrics.MetricsService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;

/**
 * Handles HTTP requests for the metrics endpoint.
 */
public class MetricsEndpointHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

  private final MetricsService metricsService;
  private final String path;

  public MetricsEndpointHandler(MetricsService metricsService, String path) {
    this.metricsService = metricsService;
    this.path = path;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
    if (req.method() != HttpMethod.GET) {
      sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
      return;
    }

    if (!req.uri().equals(path)) {
      sendError(ctx, HttpResponseStatus.NOT_FOUND);
      return;
    }

    String content = metricsService.scrape();
    FullHttpResponse response = new DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK,
        Unpooled.copiedBuffer(content, StandardCharsets.UTF_8)
    );

    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; version=0.0.4; charset=utf-8");
    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }

  private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }
}
