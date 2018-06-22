package com.collect.betty.config;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.net.InetSocketAddress;

/*
 * xml 리소스를 추가하려면 아래 annotation을 추가
 * @ImportResource("classpath:spring/bettyApplicationContext.xml")
 */
@Configuration
@ComponentScan("com.collect.betty.*")
@ImportResource("classpath:spring/bettyApplicationContext.xml")
@Data
public class BettyProperties implements IBettyProperty {

    @Value("${betty.boss.count:1}")
    private int bossThreadCount;

    @Value("${betty.worker.count}")
    private int workerThreadCount;

    @Value("${betty.server.http.port:8080}")
    private int port;

    // tcp 서버로 할껀지 http 로 할껀지에 따라 채널 초기화
    @Value("${betty.server.transfer.type:tcp}")
    private String transferType;

    @Value("${betty.server.transfer.maxContentLength:0}")
    private int transferMaxContentLength;

    @Value("${betty.server.transfer.websocket.path:/tmp}")
    private String transferWebsocketPath;

    @Value("${betty.server.transfer.websocket.subProtocol:subprotocol}")
    private String transferWebsocketSubProtocol;

    @Value("${betty.server.transfer.websocket.allowExtensions:false}")
    private boolean transferWebsocketAllowExtensions;

    @Value("${betty.server.log.level.pipeline:0}")
    private String logLevelPipeline;

    @Bean(name = "bossThreadCount")
    public int getBossCount() {
        return bossThreadCount;
    }

    @Bean(name = "workerThreadCount")
    public int getWorkerCount() {
        return workerThreadCount;
    }

    @Bean(name="tcpSocketAddress")
    public InetSocketAddress port() {
        return new InetSocketAddress(port);
    }

    @Bean(name="workerEventLoopGroup")
    @Scope(value = "prototype")
    public EventLoopGroup getWorkerEventLoopGroup() {
        EventLoopGroup eventLoopGroup;
        if (Epoll.isAvailable()) {
            // linux 커널 2.6이상인 경우 EPollEventLoopGroup 를 사용한다.
            eventLoopGroup = new EpollEventLoopGroup(workerThreadCount);
        } else {
            // Configure the server.
            eventLoopGroup = new NioEventLoopGroup(workerThreadCount);
        }

        return eventLoopGroup;
    }

    @Bean(name="bossEventLoopGroup")
    @Scope(value = "prototype")
    public EventLoopGroup getBossEventLoopGroup() {
        EventLoopGroup eventLoopGroup;
        if (Epoll.isAvailable()) {
            // linux 커널 2.6이상인 경우 EPollEventLoopGroup 를 사용한다.
            eventLoopGroup = new EpollEventLoopGroup(bossThreadCount);
        } else {
            // Configure the server.
            eventLoopGroup = new NioEventLoopGroup(bossThreadCount);
        }

        return eventLoopGroup;
    }

    @Bean(name="serverSocketChannel")
    @Scope(value = "prototype")
    public ServerSocketChannel getServerChannel() {
        ServerSocketChannel serverSocketChannel;
        if (Epoll.isAvailable()) {
            // linux 커널 2.6이상인 경우 EPollEventLoopGroup 를 사용한다.
            serverSocketChannel = new EpollServerSocketChannel();
        } else {
            // Configure the server.
            serverSocketChannel = new NioServerSocketChannel();
        }

        return serverSocketChannel;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}