package com.collect.betty.handler;

import com.collect.betty.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.AsciiString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public interface BettyResponseable {

//    /**
//     * defalut 요청
//     */
//    BettyResponseable NONE = new BettyResponseable() {
//        private final String helloMsg = "Hello, Betty!";
//
//        private ObjectMapper newMapper() {
//            ObjectMapper m = new ObjectMapper();
//            m.registerModule(new AfterburnerModule());
//            return m;
//        }
//
//        private int jsonLen() {
//            try {
//                return newMapper().writeValueAsBytes(new Message(helloMsg)).length;
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        @Override
//        public FullHttpResponse makeReponse(ByteBuf buf, CharSequence contentType, CharSequence contentLengh) {
//            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buf, false);
//            response.headers()
//                    .set(DATE, new AsciiString(LocalDateTime.now()
//                            .format(DateTimeFormatter.ofPattern("EEE MMM d kk:mm:ss yyyy", Locale.US))))
//                    .set(SERVER, "Response-Server")
//                    .set(CONTENT_TYPE, contentType)
//                    .set(CONNECTION, "close")
//                    .set(CONTENT_LENGTH, contentLengh);
//
//            return response;
//        }
//
//        @Override
//        public void writeReponse(ChannelHandlerContext ctx, ByteBuf byteBuf) {
//            ctx.write(makeReponse(byteBuf, APPLICATION_JSON, AsciiString.cached(String.valueOf(byteBuf.readableBytes()))));
//        }
//
//    };
//
//    FullHttpResponse makeReponse(ByteBuf buf, CharSequence coontentType, CharSequence contentLengh);
//
    void writeReponse(ChannelHandlerContext ctx, ByteBuf byteBuf);
//    void process(String uri);
}
