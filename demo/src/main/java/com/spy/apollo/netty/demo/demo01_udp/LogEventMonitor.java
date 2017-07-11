package com.spy.apollo.netty.demo.demo01_udp;

import com.spy.apollo.netty.demo.demo01_udp.protocol.LogEventDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 本机监听模式
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 14:32
 * @since 1.0
 */
@Slf4j
public class LogEventMonitor {

    private final Bootstrap      bootstrap;
    private final EventLoopGroup group;

    public LogEventMonitor(InetSocketAddress address) {
        group = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                 .channel(NioDatagramChannel.class)// udp
                 .option(ChannelOption.SO_BROADCAST, true)// 广播
                 .handler(new ChannelInitializer<Channel>() {
                     @Override
                     protected void initChannel(Channel ch) throws Exception {
                         ch.pipeline()
                           .addLast(new LogEventDecoder())
                           .addLast(new LogEventHandler());
                     }
                 })
                 .localAddress(address);
    }

    public static void main(String[] args) {
        LogEventMonitor monitor = new LogEventMonitor(new InetSocketAddress(Const.PORT));

        Channel channel = monitor.run();

        try {

            log.info("monitor is running.");
            channel.closeFuture().await();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            monitor.stop();
        }
    }

    public Channel run() {
        return bootstrap.bind().syncUninterruptibly().channel();
    }

    public void stop() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }

}
