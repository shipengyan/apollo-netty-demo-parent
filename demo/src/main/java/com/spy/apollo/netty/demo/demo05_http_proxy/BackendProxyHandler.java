package com.spy.apollo.netty.demo.demo05_http_proxy;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-13 13:29
 * @since 1.0
 */
@Slf4j
public class BackendProxyHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private final Channel inboundChannel;

    BackendProxyHandler(Channel channel) {
        this.inboundChannel = channel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.debug("channel active");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        inboundChannel.writeAndFlush(msg.retain()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    InetSocketAddress toAddress = (InetSocketAddress) inboundChannel.remoteAddress();
                    log.debug("数据发往:{}", toAddress.getHostName());
                } else {
                    future.channel().close();

                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Backend Handler destroyed!");
        FrontendProxyHandler.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        FrontendProxyHandler.closeOnFlush(ctx.channel());
    }
}
