package com.spy.apollo.netty.client.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-07 7:40
 * @since 1.0
 */
@Slf4j
public class HttpClient {

    private HttpClientHandler clientHandler = new HttpClientHandler();
    private URI    uri;
    private String url;

    public HttpClient(String url) {
        this.url = url;
    }

    public void connect() throws InterruptedException, URISyntaxException {
        uri = new URI(url);
        EventLoopGroup loopGroup = new NioEventLoopGroup();
        Bootstrap      b         = new Bootstrap();

        b.group(loopGroup)
         .channel(NioSocketChannel.class)
//         .option(ChannelOption.SO_RCVBUF, new FixedRecvByteBufAllocator(10240)) // 修改无效
//         .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(10240))
         //控制接收包大小，默认1024
//         .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(100, 2000, 20480))
         .handler(new ChannelInitializer<SocketChannel>() {
             @Override
             protected void initChannel(SocketChannel ch) throws Exception {
                 ch.pipeline()
                   .addLast(new LoggingHandler(LogLevel.INFO))
//                   .addLast(new LineBasedFrameDecoder(10240))
//                   .addLast(new LengthFieldBasedFrameDecoder(10240, 0, 2, 0, 2))
                   .addLast(new HttpRequestEncoder())
                   .addLast(new HttpResponseDecoder())
//                   .addLast("codec", new HttpClientCodec())
                   .addLast("aggegator", new HttpObjectAggregator(512 * 1024))
                   .addLast("clientHandler", clientHandler);
             }
         });
        Channel channel = b.connect(uri.getHost(), uri.getPort() < 0 ? 80 : uri.getPort()).sync().channel();
        while (!channel.isActive()) {
            Thread.sleep(1000);
        }
    }

    public String getBody() throws Exception {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
        request.headers().set(HttpHeaders.Names.HOST, uri.getHost());
        request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
        ChannelPromise promise = clientHandler.sendMessage(request);
        promise.await();
        return clientHandler.getData();
    }

}
