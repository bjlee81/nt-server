package com.collect.betty.handler;

import com.collect.betty.model.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class CsAllocResponse extends AbstractBettyResponse {

    public CsAllocResponse(ChannelHandlerContext ctx, HttpRequest request) {
        super(ctx, request);
    }

    @Override
    protected Message makeReponseJson(HttpRequest request) {
        String fullUri = request.uri();
        String[] mappingUrl = fullUri.split("/", fullUri.lastIndexOf("/"));
        // Message 형태의 json 응답을 생성
        return null;
    }

    @Override
    public String toString() {
        return null;
    }
}
