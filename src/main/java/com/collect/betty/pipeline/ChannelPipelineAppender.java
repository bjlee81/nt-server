package com.collect.betty.pipeline;

import com.collect.betty.type.ProtocolType;
import io.netty.channel.ChannelPipeline;

import java.util.concurrent.ScheduledExecutorService;

public class ChannelPipelineAppender {

    private final ProtocolType protocolType;
    private ScheduledExecutorService service;
    private BettyTransferPipeline bettyTransferPipeline;

    public ChannelPipelineAppender(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    public void addHandler(ChannelPipeline channelPipeline) {
        switch (protocolType) {
            case HTTP:
            case HTTPS:
                bettyTransferPipeline = new HttpPipeline(null);
                break;
            case WEBSOCKET:
                bettyTransferPipeline = new WebsocketPipeline();
                break;
            case TCP:
            default:
                bettyTransferPipeline = new TcpPipeline();
                break;
        }

        bettyTransferPipeline.initChannel(channelPipeline);
    }
}
