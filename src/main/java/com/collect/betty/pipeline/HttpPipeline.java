package com.collect.betty.pipeline;

import com.collect.betty.handler.BettyApiRequestParser;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;

public class HttpPipeline extends BettyTransferPipeline {
    private final SslContext sslCtx;

    private int maxInitLineLeng = 4096;
    private int maxHeaderSize = 8192;
    private int maxChunkSize = 8192;
    private boolean validateHdrs = false;

    public HttpPipeline(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(ChannelPipeline p) {
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(p.channel().alloc()));
        }
        // http 요청처리 디코더
        p.addLast("decoder", new HttpRequestDecoder(maxInitLineLeng, maxHeaderSize, maxChunkSize, validateHdrs));
        // http 프로토콜에서 발생하는 메시지 파편화를 처리하는 디코더
        // 프로토콜을 구성하는 데이터가 나뉘어서 수신되었을 때 데이터를 하나로 합쳐주는 역할을 수행
        // maxContentLength 는 한꺼번에 처리 가능한 최대 데이터 크기. 초과될 경우 TooLongFrameException 이 발생
        p.addLast("aggregator", new HttpObjectAggregator(100 * 1024 * 1024));
        // http 요청의 처리 결과를 클라이언트로 전송할 때 http 프로토콜로 변환해주는 인코더
        p.addLast("encoder", new HttpResponseEncoder());
        // http 프로토콜의 송수신 http 본문데이터를 gzip으로 수행하도록 한다. in/out 전부 관여
        p.addLast("compressor", new HttpContentCompressor());
        p.addLast("logger", new LoggingHandler(LogLevel.valueOf(getLogLv())));
        // client 로 부터 수신된 http 데이터에서 헤더와 데이터 값을 추출하여 업무처리 클래스로 분기한다. controller 역할을 담당
        p.addLast("handler", new BettyApiRequestParser());
    }
}
