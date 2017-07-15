package com.spy.apollo.netty.demo.demo11_native;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
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

    public void sendMsg(Object obj) {
        if (ctx != null && ctx.channel().isActive()) {
            log.debug("client send msg {}", obj);
            ctx.writeAndFlush(Unpooled.buffer().writeBytes(JSON.toJSONString(obj).getBytes(CharsetUtil.UTF_8)));
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel active");
        sendMsg("hello world!");
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
