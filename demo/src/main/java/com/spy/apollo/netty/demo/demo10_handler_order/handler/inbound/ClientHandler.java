package com.spy.apollo.netty.demo.demo10_handler_order.handler.inbound;

import com.alibaba.fastjson.JSON;
import com.spy.apollo.netty.demo.common.Const;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 21:58
 * @since 1.0
 */
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;


    public void sendMsg(Object obj) {
        if (ctx != null && ctx.channel().isActive()) {
            log.debug("client send msg {}", obj);
            ByteBuf byteBuf  = Unpooled.buffer().writeBytes(JSON.toJSONString(obj).getBytes(CharsetUtil.UTF_8));
            ByteBuf byteBuf1 = byteBuf.copy();

            ctx.writeAndFlush(byteBuf);
            ctx.channel().writeAndFlush(byteBuf1);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.ctx = ctx;
        ctx.attr(Const.STATE).set("abc");
        ctx.channel().attr(Const.STATE).set("abc2");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        log.debug("channel active");
        ctx.channel().eventLoop().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                sendMsg("hello world!");
            }
        }, 1, 10, TimeUnit.SECONDS);

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
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

}
