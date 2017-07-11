package com.spy.apollo.netty.spring.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.spy.apollo.netty.spring.biz.service.ServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static io.netty.buffer.Unpooled.buffer;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-06 9:02
 * @since 1.0
 */
@Slf4j
@Component
public class ServerHandlerImpl implements ServerHandler {

    private ChannelHandlerContext ctx;

    private ChannelPromise promise;

    private JSONObject data;

    @Override
    public void sendMsg(Object obj) {
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            log.debug("server send msg");

            ByteBuf byteBuf = Unpooled.buffer().writeBytes(JSON.toJSONString(obj).getBytes());
            ctx.writeAndFlush(buffer().writeBytes(byteBuf));
        }
    }

    @Override
    public JSONObject sendMsgSync(Object obj) {
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            log.debug("server send msg");

            ByteBuf byteBuf = Unpooled.buffer().writeBytes(JSON.toJSONString(obj).getBytes());
            promise = ctx.writeAndFlush(byteBuf).channel().newPromise();

            //发送消息不需要release msg
            try {
                promise.await();

                return this.data;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        return null;
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel registered");
        this.ctx = ctx;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel unregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel inactive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("channel read");

        ByteBuf in = (ByteBuf) msg;

        String readStr = in.toString(CharsetUtil.UTF_8);
        log.debug("Server received: {}", readStr);

        // relase bytebuf
        ReferenceCountUtil.release(in);

        //convert to json
        data = JSON.parseObject(readStr);

        if (promise != null) {
            promise.setSuccess();
            promise = null;
        }


    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("channel read complete");
        ctx.flush();
        //ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);//4
        //.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("user event triggered [{}]", evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            log.info("event={}", event.state());

            switch (event.state()) {
                case READER_IDLE:
                    break;
                case WRITER_IDLE:
                    break;
                case ALL_IDLE:
                    break;
            }
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        log.info("channel writability changed");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("handler added");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("handler removed");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception caught");
        cause.printStackTrace();                //5
        ctx.close();
    }


}
