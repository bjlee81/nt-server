package com.collect.betty.config;

import lombok.Getter;

public class FixBettyProperties implements IBettyProperty {
    @Getter
    private int bossThreadCount = 1;
    @Getter
    private int workerThreadCount = 16;
    @Getter
    private int port = 8080;
    @Getter
    private String transferType = "tcp";
    @Getter
    private int transferMaxContentLength = 0;


    @Override
    public String getLogLevelPipeline() {
        return "INFO";
    }

    @Override
    public String getTransferWebsocketPath() {
        return "/tmp";
    }

    @Override
    public String getTransferWebsocketSubProtocol() {
        return "subprotocol";
    }

    @Override
    public boolean isTransferWebsocketAllowExtensions() {
        return false;
    }
}
