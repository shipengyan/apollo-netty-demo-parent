package com.spy.apollo.netty.client.biz.impl;

import com.spy.apollo.netty.client.biz.Client;
import com.spy.apollo.netty.client.biz.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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
 * @version 1.0 2017-07-06 10:12
 * @since 1.0
 */
@Slf4j
@Component
public class ClientImpl implements Client {


    @Value("${apollo.server.host}")
    private String host;
    @Value("${apollo.server.port}")
    private int    port;

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    @Autowired
    private ClientHandler clientHandler;

    private int MAX_RETRY_CONNECT = 20;
    private int reconnect         = 0;

    private Long readerIdleTime = 10L;
    private Long writerIdleTime = 10L;
    private Long allIdleTime    = 20L;

    @Override
    public void start() {
        group = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(group)
                 .channel(NioSocketChannel.class)
                 .remoteAddress(new InetSocketAddress(host, port))
                 .option(ChannelOption.SO_KEEPALIVE, true)
                 .option(ChannelOption.TCP_NODELAY, true)
                 .handler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     public void initChannel(SocketChannel ch) throws Exception {
                         ch.pipeline().addLast("timeout", new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime, TimeUnit.SECONDS));

                         //client端 发的是request，因此要编码
                         //client端 收的是Reponse，因此要解码
                         //   ch.pipeline().addLast(new Encode(Message.class));
                         ch.pipeline().addLast(clientHandler);
                     }
                 });

        connect();
    }

    @Override
    public void stop() {
        if (group != null && !group.isShutdown()) {
            if (group.isShuttingDown()) {
                log.warn("netty client is shutdowning, plz wait");
            } else {
                group.shutdownGracefully();
            }
        }
    }

    @Override
    public void reconnect() {
        if (group == null) {
            start();
        } else {
            connect();
        }
    }


    private void connect() {
        ChannelFuture future = bootstrap.connect();      //6

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.debug("future={}", future);
                if (future.isSuccess()) {

                    reconnect = 0;
                    log.info("netty client connect {}:{} successfully.", host, port);

                } else {
                    if (reconnect > MAX_RETRY_CONNECT) {
                        log.warn("has reach max reconnection count[{}]", MAX_RETRY_CONNECT);
                        return;
                    }
                    reconnect++;
                    log.debug("\nreconnect {} time....\n", reconnect);

                    TimeUnit.MILLISECONDS.sleep(30);
                    start();
                }
            }
        });
    }
}
