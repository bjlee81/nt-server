package com.collect.betty.handler;

import com.collect.betty.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.FastThreadLocal;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
public abstract class AbstractBettyResponse {

    private static final byte[] STATIC_PLAINTEXT = "Hello, World!".getBytes(CharsetUtil.UTF_8);
    private static Message errMsg() {
        return new Message("Error!!!");
    }
    private static int errResponseLen() {
        try {
            return newMapper().writeValueAsBytes(errMsg()).length;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private static final FastThreadLocal<DateTimeFormatter> FORMAT = new FastThreadLocal<DateTimeFormatter>() {
        @Override
        protected DateTimeFormatter initialValue() {
            return DateTimeFormatter.ofPattern("EEE MMM d kk:mm:ss yyyy", Locale.US);
        }
    };
    private final ChannelHandlerContext ctx;
    private final CharSequence contentType;
    private final CharSequence SERVER_NAME = AsciiString.cached("Betty/1.0");
    private final HttpResponseStatus OK_STATUS = HttpResponseStatus.OK;
    private HttpRequest request;
    private Map<String, String> requestHeaders = new HashMap();
    // context length
    private CharSequence contentLength;
    private CharSequence defaultContentType = "application/json";

    protected AbstractBettyResponse(@NotNull ChannelHandlerContext ctx, @NotNull HttpRequest httpRequest, CharSequence contentType) {
        this.ctx = ctx;
        this.request = httpRequest;
        this.contentType = contentType == null ? defaultContentType : contentType;
    }

    protected AbstractBettyResponse(@NotNull ChannelHandlerContext ctx, @NotNull HttpRequest httpRequest) {
        this(ctx, httpRequest, null);
    }

    private static ObjectMapper newMapper() {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new AfterburnerModule());
        return m;
    }

    private FullHttpResponse makeResponse(ByteBuf buf, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, buf, false);
        response.headers()
                .set(DATE, new AsciiString(LocalDateTime.now().format(FORMAT.get())))
                .set(SERVER, SERVER_NAME)
                .set(CONTENT_TYPE, contentType)
                .set(CONNECTION, "close")
                .set(CONTENT_LENGTH, contentLength);
        return response;
    }

    protected void process() {
        // request 에 대한 상세 응답 구조 생성
        ByteBuf contentBuf = null;
        HttpResponseStatus status = null;
        try {
            saveHeaders();
            byte[] content = newMapper().writeValueAsBytes(makeReponseJson(request));

            // 길이 설정
            contentLength = AsciiString.cached(String.valueOf(content.length));
            status = OK_STATUS;
            contentBuf = Unpooled.wrappedBuffer(content);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage());
            // TODO : 각 상황별 status 변경 처리 필요
            contentBuf = getErrorContent(e);
            contentLength = AsciiString.cached(String.valueOf(errResponseLen()));
            status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        } finally {
            ctx.write(makeResponse(contentBuf, status), ctx.voidPromise());
        }
    }

    private ByteBuf getErrorContent(JsonProcessingException e) {
        ByteBuf buf = null;
        try {
            // TODO : action e

            buf = Unpooled.wrappedBuffer(newMapper().writeValueAsBytes(errMsg()));
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }

        return buf;
    }

    /**
     * json 형태의 response 문자열 생성
     *
     * @param request
     * @return
     */
    protected abstract Message makeReponseJson(HttpRequest request);

    private void saveHeaders() {
        HttpHeaders headers = request.headers();
        Iterator<Map.Entry<String, String>> iterator = headers.iteratorAsString();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            requestHeaders.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public abstract String toString();
}
