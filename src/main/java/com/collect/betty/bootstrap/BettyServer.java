package com.collect.betty.bootstrap;

import com.collect.betty.handler.BettyChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

/**
 * server bootstrap을 설정
 */
@Component
@Slf4j
public class BettyServer {
    static final boolean SSL = System.getProperty("ssl") != null;

    static {
        ResourceLeakDetector.setLevel(Level.DISABLED);
    }

    @Autowired
    private ApplicationContext context;

    @Autowired
    @Qualifier("tcpSocketAddress")
    private InetSocketAddress address;

    @Autowired
    @Qualifier("workerEventLoopGroup")
    private EventLoopGroup workerEventLoopGroup;

    @Autowired
    @Qualifier("bossEventLoopGroup")
    private EventLoopGroup bossEventLoopGroup;

    @Autowired
    @Qualifier("serverSocketChannel")
    private ServerSocketChannel serverSocketChannel;

    private Channel ch;

    public void start() throws Exception {
        doRun(bossEventLoopGroup, workerEventLoopGroup, serverSocketChannel.getClass());
    }

    private void doRun(EventLoopGroup loopGroup, EventLoopGroup childLoopGroup, Class<? extends ServerChannel> serverChannelClass) throws InterruptedException {
        try {
            ServerBootstrap b = new ServerBootstrap();

            if (serverChannelClass.equals(EpollServerSocketChannel.class)) {
                b.option(EpollChannelOption.SO_REUSEPORT, true);
            }

            // 동시에 수용 가능한 서버 소켓 연결 요청 수. 동접과 무관
            // 무작정 크면 클라이언트에서 연결이 폭주할때 연결 타임아웃이 발생할 수 있고
            // 작으면 클라이언트 연결을 생성하지 못하는 경우가 발생할 수 있음
            b.option(ChannelOption.SO_BACKLOG, 8192);
            // time_wait 상태의 포트를 서버 소켓에 바인드할 수 있게 한다. defalut : false
            b.option(ChannelOption.SO_REUSEADDR, true);
            // boss, worker 그룹 설정
            // boss 그룹은 클라이언트의 연결을 처리하는 그룹
            // worker 그룹은 연결된 클라이언트의 소켓으로부터 데이터 입출력 및 이벤트 처리를 담당하는 스레드 그룹.
            // worker 그룹의 수를 선언하지 않으면 cpu core 에 따라 자동 설정(2배수로 설정)
            // 인자를 1개만 주면 같은 EventLoopGroup 으로 boss/worker 를 선언한다.
            b.group(loopGroup, childLoopGroup)
                    .handler(new LoggingHandler(LogLevel.INFO))     // 서버소켓 채널에서 발생한 이벤트만을 처리
                    .channel(serverChannelClass)
                    .childHandler(new BettyChannelInitializer(childLoopGroup));        // 이벤트 처리를 위한 채널 핸들러
            b.childOption(ChannelOption.SO_REUSEADDR, true);

            // 내부적으로 ChannelFuture 객체를 반환하고 sync() 는 awit()를 내부적으로 처리한다.
            ch = b.bind(address).sync().channel();

            LOGGER.info("Httpd started. Listening on: {}", address.toString());

            ChannelFuture cf = ch.closeFuture();

            // sync()를 호출하면 코드가 블로킹되어 이후의 코드가 실행되지 않는다.
            // ssl 처리를 하려면 sync() 를 호출하지 않고 블로킹모드가 되지 않도록하게 한다.
            cf.sync();

            makeSslContext();

        } finally {
            loopGroup.shutdownGracefully().sync();
            childLoopGroup.shutdownGracefully().sync();
        }
    }

    /**
     * ssl 처리를 위한 server 설정을 추가한다.
     * 다른 port 추가를 위한 server 처리도 아래와 같이 bootstrap을 추가하는 방법으로 하면 된다.
     *
     */
    private void makeSslContext() {
        final SslContext sslContext;
        try {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossEventLoopGroup, workerEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new BettyChannelInitializer(sslContext));

            Channel ch = b.bind(8443).sync().channel();
            ChannelFuture cf = ch.closeFuture();
            cf.sync();

        } catch (CertificateException | InterruptedException e) {
            e.printStackTrace();
        } catch (SSLException e) {
            e.printStackTrace();
        }
    }


    @PreDestroy
    public void stop() throws Exception {
        if (ch != null) {
            ch.close();
            ch.parent().close();
        }
    }

}
