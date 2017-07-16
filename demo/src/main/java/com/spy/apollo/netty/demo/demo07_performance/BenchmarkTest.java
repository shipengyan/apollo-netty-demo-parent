package com.spy.apollo.netty.demo.demo07_performance;

import com.spy.apollo.netty.demo.common.Const;
import com.spy.apollo.netty.demo.demo00_helloworld.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * 性能测试
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-16 11:20
 * @since 1.0
 */
@Slf4j
public class BenchmarkTest {

    Bootstrap bootstrap;

    @Before
    public void before() {
        NioEventLoopGroup group = new NioEventLoopGroup(1);

        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                     .channel(NioSocketChannel.class)
                     //优化
                     .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
                     .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)

                     .option(ChannelOption.SO_KEEPALIVE, true)
                     .option(ChannelOption.TCP_NODELAY, true)
                     .handler(new LoggingHandler(LogLevel.INFO))
                     .handler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         public void initChannel(SocketChannel ch) throws Exception {
                             //ch.pipeline().addLast("timeout", new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime, TimeUnit.SECONDS));


                             ch.pipeline()

                               .addLast("encoder", new LengthFieldPrepender(4, false))
                               .addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))

                               .addLast("clientHandler", new ClientHandler());
                         }
                     });
        } catch (Exception e) {

            log.debug("", e);
        }
    }

    @Test
    public void run() throws IOException {
        int MAX_CONNECTION = 10000;
        for (int i = 0; i < MAX_CONNECTION; i++) {
            bootstrap.connect(Const.HOST, Const.PORT);
        }

        System.in.read();
    }

}
