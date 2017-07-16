package com.spy.apollo.netty.demo.common;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-14 21:59
 * @since 1.0
 */
@Slf4j
public class MsgUtil {
    /**
     * 发送消息
     *
     * @param ctx
     * @param obj
     */
    public static void send(ChannelHandlerContext ctx, Object obj) {
        if (ctx != null && ctx.channel().isActive()) {
            log.debug("send msg {}", obj);
            ByteBuf byteBuf = Unpooled.buffer().writeBytes(JSON.toJSONString(obj).getBytes(CharsetUtil.UTF_8));

            ctx.writeAndFlush(byteBuf);
        }
    }
}
