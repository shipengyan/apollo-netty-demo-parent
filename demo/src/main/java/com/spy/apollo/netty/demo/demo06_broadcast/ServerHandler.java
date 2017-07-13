package com.spy.apollo.netty.demo.demo06_broadcast;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 21:34
 * @since 1.0
 */
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;

    //TODO
    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());

        String remote = ctx.channel().remoteAddress().toString();
        String msg    = String.format("欢迎 %s 加入聊天室", remote);

        channels.writeAndFlush(Unpooled.buffer().writeBytes(msg.getBytes(CharsetUtil.UTF_8)));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;

        String readStr = in.toString(CharsetUtil.UTF_8);
        log.debug("Server received: {}", readStr);

        ReferenceCountUtil.release(msg);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
