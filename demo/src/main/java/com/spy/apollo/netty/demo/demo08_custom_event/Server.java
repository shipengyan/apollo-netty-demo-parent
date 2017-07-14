package com.spy.apollo.netty.demo.demo08_custom_event;

import com.spy.apollo.netty.demo.common.Const;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 21:34
 * @since 1.0
 */
@Slf4j
public class Server {
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;

    private Long readerIdleTime = 10L;
    private Long writerIdleTime = 10L;
    private Long allIdleTime    = 20L;

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        server.start();
    }

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .localAddress(new InetSocketAddress(Const.PORT))
             .option(ChannelOption.SO_BACKLOG, 100)
             //优化
             .childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
             .childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)

             .childOption(ChannelOption.SO_KEEPALIVE, true) //长连接
             .childOption(ChannelOption.TCP_NODELAY, true) // 为了尽可能发送大块数据，避免网络中充斥着许多小数据块。
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline()
                       .addLast(new LoggingHandler(LogLevel.INFO))

                       .addLast("timeout", new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime, TimeUnit.SECONDS))

                       .addLast("encoder", new LengthFieldPrepender(4, false))
                       .addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))

                       .addLast("serverHandler", new ServerHandler());
                 }
             });

            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
        }
    }
}
