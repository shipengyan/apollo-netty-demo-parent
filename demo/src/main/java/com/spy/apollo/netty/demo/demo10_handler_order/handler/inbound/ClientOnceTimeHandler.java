package com.spy.apollo.netty.demo.demo10_handler_order.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-14 16:01
 * @since 1.0
 */
@Slf4j
public class ClientOnceTimeHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("channel active ");

        log.info("something is done");
        ChannelPipeline p1 = ctx.pipeline();
        ChannelPipeline p2 = ctx.channel().pipeline();

        log.debug("p1={}", p1);
        log.debug("p2={}", p2);

        ctx.channel().pipeline().remove(this);

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        log.debug("handler removed");
    }
}
