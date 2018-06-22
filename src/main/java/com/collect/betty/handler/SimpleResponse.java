package com.collect.betty.handler;

import com.collect.betty.type.ApiType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleResponse implements IBettyResponse {
    private final ChannelHandlerContext ctx;
    private final HttpRequest request;

    public SimpleResponse(ChannelHandlerContext ctx, HttpRequest request) {
        super();
        this.ctx = ctx;
        this.request = request;
    }

    @Override
    public AbstractBettyResponse createResponseObject(ApiType apiType) {
        try {
            String uri = request.uri();
            String prefixUri = getRequestPrefixUri(uri);

            switch (prefixUri) {
                case "plaintext":
                    return new PlainTextDummyResponse(ctx, request);
                case "json":
                    return new JsonDummyResponse(ctx, request);
                case "mobile":
                    return new CsAllocResponse(ctx, request);
                case "bjlee":
                    return new BjleeTempResponse(ctx, request);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return new HelloDefaultResponse(ctx, request);
    }

    private String getRequestPrefixUri(String uri) {
        String tmpUri = uri.startsWith("/") ? uri.replaceFirst("/", "") : uri;

        // 2 size 로 split 해서 앞에 split string 을 반환
        return tmpUri.split("/", 2)[0];
    }

}
