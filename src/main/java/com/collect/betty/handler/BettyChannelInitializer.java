package com.collect.betty.handler;

import com.collect.betty.config.BettyProperties;
import com.collect.betty.pipeline.ChannelPipelineAppender;
import com.collect.betty.type.ProtocolType;
import com.collect.betty.util.BeanUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

import java.util.concurrent.ScheduledExecutorService;

/**
 * 새 커넥션이 들어올 때마다 ChannelInitalizer 를 상속받은 객체가 호출됨
 *
 * @Sharable
 */
public class BettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslContext;
    private BettyProperties bettyProperties = BeanUtil.getBean(BettyProperties.class);

    private ScheduledExecutorService service;

    public BettyChannelInitializer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    public BettyChannelInitializer() {
        this.sslContext = null;
    }

    public BettyChannelInitializer(ScheduledExecutorService service) {
        this.sslContext = null;
        this.service = service;
    }

    /**
     * ChannelInitializer 에서 클라이언트 소켓 채널이 생성될 때 자동으로 호출되는 함수
     * 채널에 대한 파이프라인(체이닝라인)을 만들고 처리를 위한 핸들러들을 생성한다.
     *
     * @param channel
     * @throws Exception
     */
    @Override
    public void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline channelPipeline = channel.pipeline();

        /*
         * 데이터가 수신되었을 때의 Netty 동작
         *
         * 1. 이벤트루프가 채널 파이프라인에 등록된 첫 번째 이벤트 핸들러를 가져옴
         * 2. 이벤트 핸들러에 데이터 수신에 대한 이벤트 메서드가 구현되어 있으면 실행
         * 3. 수신 이벤트 메서드가 구현되어 있지 않으면 다음 이벤트 핸들러 가져옴
         * 4. 마지막으로 추가된 파이프까지 반복 수행
         *
         * # 중요!!
         * 파이프라인에서 이벤트가 한번 소모되면 다음 핸들러에서 해당 이벤트 메서드를 구현해도 이벤트는 사라진 상태이다.
         * 따라서 여러개의 핸들러에서 이벤트(read 등)를 처리하려할 때는 ctx.fireChannelRead(msg); 를 사용하여
         * 채널 파이프라인에 해당 이벤트를 다시 발생시켜야한다. 접두어 fire 가 붙으면 이벤트를 발생시킨다.
         *
         */
        String type = bettyProperties == null ? "tcp" : bettyProperties.getTransferType();
        ProtocolType protocolType = ProtocolType.valueOf(type.toUpperCase());

        /**
         * 파이프라인에 등록되는 핸들러들은 ChannelInboundHandler, ChannelOutboundHandler 를 구현한 구현체가 되어야한다.
         * 모든 OutboundHandler 는 ChannelHandlerContext 객체를 인수로 받는다.
         *
         * ChannelHandlerContext 는 채널의 입출력 처리, 채널 파이프라인에 대한 상호작용을 담당한다.
         * 파이프라인 수정, 사용자 이벤트 발생처리 등을 할수 있다.
         *
         * encoder(인코딩해서 다른 곳으로 내보내는 역할)는 아웃바운드, decoder(디코딩해서 내부에서 사용하는 역할)는 인바인드로 표현한다.
         *
         * 인코더는 전송할 데이터를 전송 프로토콜에 맞추어 변환하는 작업을 수행하고
         * 디코더는 반대의 작업을 수행한다.
         *
         */
        ChannelPipelineAppender pipelineFactory = new ChannelPipelineAppender(protocolType);
        pipelineFactory.addHandler(channelPipeline);
    }

}
