package com.spy.apollo.netty.demo.demo02_biz_logic;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 模块名
 * 慎重使用 ChannelInboundHandlerAdapter，建议使用ChannelInBoundHandler
 * @author shi.pengyan
 * @version 1.0 2017-07-11 21:58
 * @since 1.0
 */
@Slf4j
public class ClientBizHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;


    public void sendMsg(Object obj) {
        if (ctx != null && ctx.channel().isActive()) {
            ctx.writeAndFlush(Unpooled.buffer().writeBytes(JSON.toJSONString(obj).getBytes(CharsetUtil.UTF_8)));
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.ctx = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("thread name={}", Thread.currentThread().getName());
        ctx.executor().execute(new Runnable() {
            @Override
            public void run() {
                log.debug("thread name={}", Thread.currentThread().getName());
                log.debug("runnable");

                try {
                    TimeUnit.SECONDS.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("done...");

            }
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

}
