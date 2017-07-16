package com.spy.apollo.netty.demo.demo10_handler_order.handler.inbound;

import com.spy.apollo.netty.demo.common.Const;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 21:58
 * @since 1.0
 */
@Slf4j
public class ClientHandler2 extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.ctx = ctx;

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel active");

        String state = ctx.attr(Const.STATE).get();
        log.info("state={}", state);

        String state2 = ctx.channel().attr(Const.STATE).get();
        log.info("state2={}", state2);
        super.channelActive(ctx);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        log.debug("read");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
