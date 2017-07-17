package com.spy.apollo.netty.demo.demo09_file;

import com.spy.apollo.netty.demo.common.Const;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 21:58
 * @since 1.0
 */
@Slf4j
public class Client {
    @Setter
    private String host;
    @Setter
    private int    port;

    private EventLoopGroup group;
    private Bootstrap      bootstrap;


    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();

        client.setHost(Const.HOST);
        client.setPort(Const.PORT);

        client.start();
    }

    public void start() throws InterruptedException {
        group = new NioEventLoopGroup(1);

        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                     .channel(NioSocketChannel.class)
                     .remoteAddress(new InetSocketAddress(host, port))
                     //优化
                     .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
                     .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)

                     .option(ChannelOption.SO_KEEPALIVE, true)
                     .option(ChannelOption.TCP_NODELAY, true)
                     .handler(new LoggingHandler(LogLevel.INFO))
                     .handler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         public void initChannel(SocketChannel ch) throws Exception {


                             ch.pipeline()
                               .addLast(new LoggingHandler(LogLevel.INFO))
//                               .addLast("encoder", new LengthFieldPrepender(4, false))
//                               .addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
//                               .addLast(new ChunkedWriteHandler())

//                               .addLast("encoder", new ObjectEncoder())
//                               .addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)))

                               .addLast("clientHandler", new ClientHandler());
                         }
                     });

            ChannelFuture f = bootstrap.connect().sync();        //6

            f.channel().closeFuture().sync();            //7
        } finally {
            group.shutdownGracefully().sync();            //8
        }

    }

}
