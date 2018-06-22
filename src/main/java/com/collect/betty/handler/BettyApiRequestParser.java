package com.collect.betty.handler;

import com.collect.betty.dispatcher.ApiRequest;
import com.collect.betty.dispatcher.ServiceDispatcher;
import com.collect.betty.type.ApiType;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.hsqldb.Tokens.SERVER_NAME;

/**
 * only use http server handler
 */
@NoArgsConstructor
@Slf4j
public class BettyApiRequestParser extends SimpleChannelInboundHandler<FullHttpMessage> {

    private HttpRequest request;
    // 업무처리 결과저장될 json object
    private JsonObject apiResult;

    // Disk
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    // 사용자가 전송한 http 요청의 content 를 추출할 디코더
    private HttpPostRequestDecoder decoder;

    // 업무처리 클래스로의 전달을 위한 map 객체
    private Map<String, Object> reqData = new HashMap();

    // api에 대해 사용할 header set
    private static final Set<String> usingHeader = new HashSet<String>();

    // 사용하는 header 선언
    static {
        usingHeader.add("token");
        usingHeader.add("email");
        usingHeader.add("user-agent");
        usingHeader.add("betty");
    }


    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpMessage msg) throws Exception {
        // Request header 처리.
        if (msg instanceof HttpRequest) {
            this.request = (HttpRequest) msg;

            /*
             * HTTP 상태코드 100(계속): 요청자는 요청을 계속해야 한다.
             * 서버는 이 코드를 제공하여 요청의 첫 번째 부분을 받았으며 나머지를 기다리고 있음을 나타낸다.
             */
            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            HttpHeaders headers = request.headers();
            if (!headers.isEmpty()) {
                for (Map.Entry<String, String> h : headers) {
                    String key = h.getKey();
                    if (usingHeader.contains(key)) {
                        reqData.put(key, h.getValue());
                    }
                }
            }

            reqData.put("REQUEST_URI", request.uri());
            reqData.put("REQUEST_METHOD", request.method().name());
        }

        // Request content 처리.
        if (msg instanceof HttpContent) {

            // params 추출
            parseRequestParameters();

            if (msg instanceof LastHttpContent) {
                LOGGER.debug("LastHttpContent message received!!" + request.uri());
                LastHttpContent trailer = msg;
                // 본문데이터 추출
                readPostData();

                // 서비스 처리를 위한 서비스 dispatcher call
                ApiRequest service = ServiceDispatcher.doDispatch(reqData);
                try {
                    service.executeService();
                    // 처리 결과 할당
                    apiResult = service.getApiResult();
                } finally {
                    reqData.clear();
                }

                if (!writeResponse(trailer, ctx)) {
                    // If keep-alive is off, close the connection once the
                    // content is fully written.
                    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                }
                reset();
            }
        }
    }

    /**
     * http GET 메소드에 대한 uri 데이터를 추출하여 reqData 에 저장한다.
     */
    private void readHttpParamsData() {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        reqData.put("REQUEST_PARAMS", queryStringDecoder.parameters());
    }

    private void reset() {
        request = null;
    }

    /**
     * request 에 대한 내용을 parse 한다.
     *
     * @throws IOException
     */
    private void parseRequestParameters() throws IOException {
        String uri = request.uri();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        /*if (!queryStringDecoder.path().equals(routePath) && !uri.contains("?")) {
            // this happens if we're using / to start the params
            uri = routePath + "?" + uri.substring(routePath.length() + 1);
            queryStringDecoder = new QueryStringDecoder(uri);
        }*/
//        this.requestPath = queryStringDecoder.path();
//        this.requestParameters = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        final Map<String, List<String>> reqs = queryStringDecoder.parameters();
        for (String k : reqs.keySet()) {
            for (String v : reqs.get(k)) {
                try {
                    jsonObject.accumulate(k, v);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        // Add POST parameters
        if (request.method() != POST)
            return;

        final HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(
                new DefaultHttpDataFactory(false), request);
        try {
            while (decoder.hasNext()) {
                InterfaceHttpData httpData = decoder.next();

                if (httpData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    Attribute attribute = (Attribute) httpData;
                    try {
                        jsonObject.accumulate(attribute.getName(), attribute.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    attribute.release();
                }
            }
        } catch (HttpPostRequestDecoder.EndOfDataDecoderException ex) {
            // Exception when the body is fully decoded, even if there is still data
        } finally {
            decoder.destroy();
        }
    }

    /**
     * http 본문에서 http post 데이터만(헤더 key-value) 추출한다.
     */
    private void readPostData() {
        try {
            decoder = new HttpPostRequestDecoder(factory, request);
            for (InterfaceHttpData data : decoder.getBodyHttpDatas()) {
                if (InterfaceHttpData.HttpDataType.Attribute == data.getHttpDataType()) {
                    try {
                        // decoder 를 통해 추출된 정보를 attribute 로 캐스팅
                        Attribute attribute = (Attribute) data;
                        reqData.put(attribute.getName(), attribute.getValue());
                    } catch (IOException e) {
                        LOGGER.error("BODY Attribute: " + data.getHttpDataType().name(), e);
                        return;
                    }
                } else {
                    LOGGER.info("BODY data : " + data.getHttpDataType().name() + ": " + data);
                }
            }
        } catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (decoder != null) {
                decoder.destroy();
            }
        }
    }

    /**
     * 서비스의 처리결과를 client 채널의 송신 버퍼에 기록
     *
     * @param currentObj
     * @param ctx
     * @return
     */
    private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
        // Decide whether to close the connection or not.
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                currentObj.decoderResult().isSuccess() ? OK : BAD_REQUEST, Unpooled.copiedBuffer(
                apiResult.toString(), CharsetUtil.UTF_8));

        response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // -
            // http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        // Write the response.
        ctx.write(response);

        return keepAlive;
    }

    /**
     * Writes a 100 Continue response.
     *
     * @param ctx The HTTP handler context.
     */
    private static void send100Continue(final ChannelHandlerContext ctx) {
        ctx.write(new DefaultFullHttpResponse(
                HTTP_1_1,
                HttpResponseStatus.CONTINUE));
    }

    /**
     * Writes a HTTP response.
     *
     * @param ctx         The channel context.
     * @param request     The HTTP request.
     * @param status      The HTTP status code.
     * @param contentType The response content type.
     * @param content     The response content.
     */
    private static void writeResponse(
            final ChannelHandlerContext ctx,
            final FullHttpRequest request,
            final HttpResponseStatus status,
            final CharSequence contentType,
            final String content) {

        final byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        final ByteBuf entity = Unpooled.wrappedBuffer(bytes);
        writeResponse(ctx, request, status, entity, contentType, bytes.length);
    }

    /**
     * Writes a HTTP response.
     *
     * @param ctx           The channel context.
     * @param request       The HTTP request.
     * @param status        The HTTP status code.
     * @param buf           The response content buffer.
     * @param contentType   The response content type.
     * @param contentLength The response content length;
     */
    private static void writeResponse(
            final ChannelHandlerContext ctx,
            final FullHttpRequest request,
            final HttpResponseStatus status,
            final ByteBuf buf,
            final CharSequence contentType,
            final int contentLength) {

        // Decide whether to close the connection or not.
        final boolean keepAlive = HttpUtil.isKeepAlive(request);

        // Build the response object.
        final FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1,
                status,
                buf,
                false);

        final ZonedDateTime dateTime = ZonedDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM d kk:mm:ss yyyy", Locale.KOREA);

        final DefaultHttpHeaders headers = (DefaultHttpHeaders) response.headers();
        headers.set(HttpHeaderNames.SERVER, SERVER_NAME);
        headers.set(HttpHeaderNames.DATE, dateTime.format(formatter));
        headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(contentLength));

        // Close the non-keep-alive connection after the write operation is done.
        if (!keepAlive) {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }

//    public String body() {
//        return request.content().toString(StandardCharsets.UTF_8);
//    }

    /**
     * Writes a HTTP error response.
     *
     * @param ctx     The channel context.
     * @param request The HTTP request.
     * @param status  The error status.
     */
    private static void writeErrorResponse(
            final ChannelHandlerContext ctx,
            final FullHttpRequest request,
            final HttpResponseStatus status) {

        writeResponse(ctx, request, status, "text/plain; charset=UTF-8", status.reasonPhrase().toString());
    }


    /**
     * Writes a 500 Internal Server Error response.
     *
     * @param ctx     The channel context.
     * @param request The HTTP request.
     */
    private static void writeInternalServerError(
            final ChannelHandlerContext ctx,
            final FullHttpRequest request) {

        writeErrorResponse(ctx, request, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * request uri 에 따른 처리를 추가해준다.
     *
     * @param ctx
     * @param request
     * @throws Exception
     */
    private void process(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        SimpleResponse factory = new SimpleResponse(ctx, request);
        AbstractBettyResponse response = factory.createResponseObject(ApiType.JSON);
        response.process();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    /**
     * 수신된 메시지를 모두 읽었을 경우에 호출됨
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("response complete.");
        ctx.flush();
    }
}
