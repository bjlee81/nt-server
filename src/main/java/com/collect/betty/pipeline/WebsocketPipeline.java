package com.collect.betty.pipeline;

import com.collect.betty.handler.WebsocketHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class WebsocketPipeline extends BettyTransferPipeline {

    private int maxContentLength = 65536;

    @Override
    public void initChannel(ChannelPipeline channelPipeline) {
        channelPipeline
                .addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(maxContentLength))
                .addLast(new WebSocketServerCompressionHandler())
                .addLast(new WebSocketServerProtocolHandler(getWebSocketPath(),
                        getWebsocketSubProtocol(),
                        isWebsocketAllowExtensions()))
                .addLast(new LoggingHandler(LogLevel.valueOf(getLogLv())))
                .addLast(new WebsocketHandler());
    }
}
