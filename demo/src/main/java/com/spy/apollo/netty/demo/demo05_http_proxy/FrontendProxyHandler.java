package com.spy.apollo.netty.demo.demo05_http_proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 21:34
 * @since 1.0
 */
@Slf4j
public class FrontendProxyHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private ChannelHandlerContext ctx;
    private Bootstrap             proxyBootstrap;
    private Channel               outboundChannel;


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        if (msg.getDecoderResult().isFailure()) {
            log.error("msg decode fail, {}", msg);
            return;
        }

        String host = msg.headers().get("Host");
        int    port = 80;

        String  pattern = "(http://|https://)?([^:]+)(:[\\d]+)?";
        Pattern r       = Pattern.compile(pattern);
        Matcher m       = r.matcher(host);
        if (m.find()) {
            host = m.group(2);
            port = (m.group(3) == null) ? 80 : Integer.parseInt(m.group(3).substring(1));
        }

        final Channel inboundChannel = ctx.channel();

        proxyBootstrap = new Bootstrap();

        proxyBootstrap.group(ctx.channel().eventLoop())
                      .channel(ctx.channel().getClass())
                      //         .option(ChannelOption.AUTO_READ, false) //TODO
                      .handler(new LoggingHandler(LogLevel.INFO))
                      .handler(new ChannelInitializer<Channel>() {
                          @Override
                          protected void initChannel(Channel ch) throws Exception {
                              ch.pipeline()
                                .addLast(new HttpClientCodec())
                                .addLast(new HttpObjectAggregator(1024 * 1024))
                                .addLast(new BackendProxyHandler(inboundChannel));
                          }
                      });
        ChannelFuture future = proxyBootstrap.connect(host, port);
        outboundChannel = future.channel();

        msg.retain();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    outboundChannel.writeAndFlush(msg);
                } else {
                    inboundChannel.close();
                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel inactive");
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
