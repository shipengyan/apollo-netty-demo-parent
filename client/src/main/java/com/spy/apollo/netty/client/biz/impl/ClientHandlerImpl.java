package com.spy.apollo.netty.client.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.spy.apollo.netty.client.biz.Client;
import com.spy.apollo.netty.client.biz.ClientHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-06 9:48
 * @since 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class ClientHandlerImpl implements ClientHandler {

    @Autowired
    private Client client;

    private ChannelHandlerContext ctx;

    private JSONObject heartJson;

    private ChannelPromise promise;
    private JSONObject     data;


    @PostConstruct
    public void after() {
        heartJson = new JSONObject();
        heartJson.put("action", "heart");
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel registered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel unregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("channel active");
        this.ctx = ctx;
//        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));//2
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel inactive");

        client.reconnect();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;

        String readStr = in.toString(CharsetUtil.UTF_8);

        log.debug("Client received: {}", readStr);

        data = JSON.parseObject(readStr);
        if (promise != null) {
            promise.setSuccess();
            promise = null;
        }

        // relase bytebuf
        ReferenceCountUtil.release(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel reade complete");
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        log.debug("user event triggered");

        if (evt instanceof IdleStateEvent) {

            IdleStateEvent event = (IdleStateEvent) evt;
            //log.info("event={}", event.state());

            switch (event.state()) {
                case READER_IDLE:
                    break;
                case WRITER_IDLE:
                    log.info("heart package");
                    ctx.writeAndFlush(Unpooled.buffer().writeBytes(heartJson.toString().getBytes()));
                    break;
                case ALL_IDLE:
                    break;
            }
        }

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel writability changed");
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.debug("handler added");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug("handler removed");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.debug("exception caught");
        cause.printStackTrace();  //4
        ctx.close();
    }

    @Override
    public void sendMsg(Object obj) {
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            log.info("client send msg.");
            ByteBuf byteBuf = Unpooled.buffer().writeBytes(JSON.toJSONString(obj).getBytes());

            ctx.writeAndFlush(byteBuf);
        }
    }

    @Override
    public JSONObject sendMsgSync(Object obj) {
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            log.info("client send msg.");

            ByteBuf byteBuf = Unpooled.buffer().writeBytes(JSON.toJSONString(obj).getBytes());

            promise = ctx.writeAndFlush(byteBuf).channel().newPromise();

            try {
                promise.await();

                log.info("send msg receive data: {}", data);

                return data;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                //ReferenceCountUtil.release(byteBuf);
            }

        }

        return null;
    }
}
