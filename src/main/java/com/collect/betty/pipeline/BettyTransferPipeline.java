package com.collect.betty.pipeline;

import com.collect.betty.config.BettyProperties;
import com.collect.betty.config.FixBettyProperties;
import com.collect.betty.config.IBettyProperty;
import com.collect.betty.pipeline.ITransferPipeline;
import com.collect.betty.util.BeanUtil;

public abstract class BettyTransferPipeline implements ITransferPipeline {

    private IBettyProperty bettyProperties;

    public BettyTransferPipeline() {
        initialize();
    }

    private void initialize() {
        try {
            this.bettyProperties = BeanUtil.getBean(BettyProperties.class);
        } catch (NullPointerException npe) {
            this.bettyProperties = new FixBettyProperties();
        }
    }

    protected String getLogLv() {
        return bettyProperties.getLogLevelPipeline();
    }

    protected String getWebSocketPath() {
        return bettyProperties.getTransferWebsocketPath();
    }

    protected String getWebsocketSubProtocol() {
        return bettyProperties.getTransferWebsocketSubProtocol();
    }

    protected boolean isWebsocketAllowExtensions() {
        return bettyProperties.isTransferWebsocketAllowExtensions();
    }
}
