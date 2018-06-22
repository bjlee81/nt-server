package com.collect.betty.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * dev version.
 */
@Slf4j
@ChannelHandler.Sharable
public class WebsocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        LOGGER.info("####################################################### ");
        Map<String, Object> result = new HashMap<>();

        // 접속자 채널 정보(연결 정보)
        Channel channel = ctx.channel();

        if (!(frame instanceof TextWebSocketFrame))
            throw new UnsupportedOperationException("unsupported frame type : " + frame.getClass().getName());

        // 전송된 내용을 JSON 개체로 변환
        Map<String, Object> data;
        try {
            data = objectMapper.readValue(((TextWebSocketFrame) frame).text(), new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonParseException | JsonMappingException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

}