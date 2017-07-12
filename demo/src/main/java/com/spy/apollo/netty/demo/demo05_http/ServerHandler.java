package com.spy.apollo.netty.demo.demo05_http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 21:34
 * @since 1.0
 */
@Slf4j
public class ServerHandler implements ChannelInboundHandler {
    private ChannelHandlerContext ctx;

    private long contentLength;
    private int  readByte;
    private String data = "";

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("thread.name={}", Thread.currentThread().getName());

        if (msg instanceof HttpResponse) {

            HttpResponse response = (HttpResponse) msg;
            contentLength = Long.parseLong(response.headers().get(HttpHeaders.Names.CONTENT_LENGTH));
            readByte = 0;
            data = "";
        }

        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf     buf     = content.content();
            readByte += buf.readableBytes();
            data += buf.toString(Charset.forName("gb2312"));
            if (readByte >= contentLength) {
                //promise.setSuccess();
                log.debug("all data={}", data);
            }
            buf.release();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        data = "";
        log.debug("channel read complete");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
