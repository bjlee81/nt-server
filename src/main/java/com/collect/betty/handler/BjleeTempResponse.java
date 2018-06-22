package com.collect.betty.handler;

import com.collect.betty.model.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import javax.validation.constraints.NotNull;

public final class BjleeTempResponse extends AbstractBettyResponse {
    protected BjleeTempResponse(@NotNull ChannelHandlerContext ctx, @NotNull HttpRequest httpRequest, CharSequence contentType) {
        super(ctx, httpRequest, contentType);
    }

    public BjleeTempResponse(ChannelHandlerContext ctx, HttpRequest request) {
        super(ctx, request);
    }

    @Override
    protected Message makeReponseJson(HttpRequest request) {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }
}
