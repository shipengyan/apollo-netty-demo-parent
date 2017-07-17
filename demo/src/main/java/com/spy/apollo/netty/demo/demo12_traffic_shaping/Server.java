package com.spy.apollo.netty.demo.demo12_traffic_shaping;

import com.spy.apollo.netty.demo.common.Const;
import com.spy.apollo.netty.demo.demo12_traffic_shaping.mbean.IoAcceptorStat;
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
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import lombok.extern.slf4j.Slf4j;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
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

    private static Server                             server                             = new Server();
    private        GlobalChannelTrafficShapingHandler globalChannelTrafficShapingHandler = new GlobalChannelTrafficShapingHandler(Executors.newScheduledThreadPool(1), 1000);

    public static Server getInstance() {
        return server;
    }

    public GlobalChannelTrafficShapingHandler globalChannelTrafficShapingHandler() {
        return globalChannelTrafficShapingHandler;
    }


    public static void main(String[] args) throws InterruptedException {
        startMBean();
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
                       .addLast(globalChannelTrafficShapingHandler)

                       // 1024 is 1KB/s, so here is 100M, for very 3s
                       .addLast(new ChannelTrafficShapingHandler(1024 * 100, 1024 * 100, 3 * 1000))
                       .addLast("serverHandler", new ServerHandler());
                 }
             });

            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
        }
    }

    private static void startMBean() {
        MBeanServer    mBeanServer = ManagementFactory.getPlatformMBeanServer();
        IoAcceptorStat mbean       = new IoAcceptorStat();

        try {
            ObjectName acceptorName = new ObjectName(mbean.getClass().getPackage().getName() + ":type=IoAcceptorStat");
            mBeanServer.registerMBean(mbean, acceptorName);
        } catch (Exception e) {
            log.error("java MBean error", e);
        }
    }
}
