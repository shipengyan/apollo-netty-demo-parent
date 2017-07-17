package com.spy.apollo.netty.demo.demo05_http_fileupload;

import com.spy.apollo.netty.demo.common.Const;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
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
                     .option(ChannelOption.SO_KEEPALIVE, true)
                     .option(ChannelOption.TCP_NODELAY, true)
                     .handler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         public void initChannel(SocketChannel ch) throws Exception {
                             //ch.pipeline().addLast("timeout", new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime, TimeUnit.SECONDS));


                             ch.pipeline()

                               // client端发送request, 接收reponse
                               //.addLast("encoder", new HttpRequestEncoder())
                               //.addLast("decoder", new HttpResponseDecoder())
                               .addLast("clientCodec", new HttpClientCodec()) // 整合了上面两个

                               //.addLast("aggegator", new HttpObjectAggregator(512 * 1024))
                               // Remove the following line if you don't want automatic content decompression.
//                               .addLast(new HttpContentDecompressor())
                               // to be used since huge file transfer
                               .addLast("chunkedWriter", new ChunkedWriteHandler())
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
