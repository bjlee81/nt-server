package com.collect.betty.pipeline;

import com.collect.betty.handler.JsonHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

/**
 * dev version.
 */
public class TcpPipeline extends BettyTransferPipeline {
    private static final StringDecoder STRING_DECODER = new StringDecoder(CharsetUtil.UTF_8);
    private static final StringEncoder STRING_ENCODER = new StringEncoder(CharsetUtil.UTF_8);

    @Override
    public void initChannel(ChannelPipeline channelPipeline) {
        channelPipeline
                .addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE))
                .addLast(STRING_DECODER)
                .addLast(STRING_ENCODER)
                .addLast(new LoggingHandler(LogLevel.valueOf(getLogLv())))
                .addLast(new JsonHandler());
    }
}
