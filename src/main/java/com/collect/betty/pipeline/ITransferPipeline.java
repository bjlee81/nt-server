package com.collect.betty.pipeline;

import io.netty.channel.ChannelPipeline;

public interface ITransferPipeline {
    void initChannel(ChannelPipeline channelPipeline);
}
