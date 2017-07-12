package com.spy.apollo.netty.spring.biz.service.impl;

import com.spy.apollo.netty.spring.biz.service.Server;
import com.spy.apollo.netty.spring.biz.service.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-06 7:23
 * @since 1.0
 */
@Slf4j
@Component
public class ServerImpl implements Server {

    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;
    @Autowired
    private ServerHandler serverHandler;
    @Value("${apollo.server.port}")
    private Integer       port;
    private Long readerIdleTime = 10L;
    private Long writerIdleTime = 10L;
    private Long allIdleTime    = 20L;

    @Override
    public void start() throws InterruptedException {
        if (bossGroup != null) {
            log.warn("netty server has started.");
        }

        bossGroup = new NioEventLoopGroup(1); //more than 1 is not needed!
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() - 1);

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup) //前者用来处理accept事件，后者用于处理已经建立的连接的io
         .channel(NioServerSocketChannel.class)  //用它来建立新accept的连接，用于构造serversocketchannel的工厂类
         .option(ChannelOption.SO_BACKLOG, 100)
         .childOption(ChannelOption.SO_KEEPALIVE, true) //长连接
         .childOption(ChannelOption.TCP_NODELAY, true) // 为了尽可能发送大块数据，避免网络中充斥着许多小数据块。
         .handler(new LoggingHandler(LogLevel.INFO))
         .localAddress(new InetSocketAddress(port))
         .childHandler(new ChannelInitializer<SocketChannel>() {
             @Override
             public void initChannel(SocketChannel ch) throws Exception {
//                 ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(10240, 0, 2, 0, 2));
                 ch.pipeline().addLast("timeout", new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime, TimeUnit.SECONDS));
                 ch.pipeline().addLast("serverHandler", serverHandler);
             }
         });

        ChannelFuture f = b.bind().sync();

        log.info("started and listen on {}", f.channel().localAddress());

        f.channel().closeFuture().sync();

    }

    @Override
    public void stop() {
        log.info("stop netty server.");
        if (bossGroup != null && !bossGroup.isShutdown()) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null && !workerGroup.isShutdown()) {
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void restart() throws InterruptedException {
        log.info("restart server.");
        stop();
        start();
    }
}
