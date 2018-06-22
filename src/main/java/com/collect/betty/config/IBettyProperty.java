package com.collect.betty.config;

public interface IBettyProperty {

    String getLogLevelPipeline();

    String getTransferWebsocketPath();

    String getTransferWebsocketSubProtocol();

    boolean isTransferWebsocketAllowExtensions();
}
