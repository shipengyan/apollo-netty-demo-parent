package com.spy.apollo.netty.demo.demo12_traffic_shaping;

import com.spy.apollo.netty.demo.common.MsgUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

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


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel active");
        MsgUtil.send(ctx, "hello world!");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;

        String result = byteBuf.toString(CharsetUtil.UTF_8);
        log.debug("receive msg={}", result);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
