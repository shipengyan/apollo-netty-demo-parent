package com.spy.apollo.netty.demo.demo10_handler_order.handler.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-14 15:18
 * @since 1.0
 */
@Slf4j
public class ClientOutboundHandler2 extends ChannelOutboundHandlerAdapter {

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        super.read(ctx);
        log.debug("read");
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        log.debug("write");
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        super.flush(ctx);
        log.debug("flush");
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise);

        log.debug("connect");
    }

}
