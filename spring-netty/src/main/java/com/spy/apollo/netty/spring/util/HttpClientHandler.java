package com.spy.apollo.netty.spring.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-07 7:35
 * @since 1.0
 */
@Slf4j
public class HttpClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;
    private ChannelPromise        promise;
    private String                data;
    private long                  readByte;
    private long                  contentLength;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
    }

    public ChannelPromise sendMessage(Object message) {
        if (ctx == null)
            throw new IllegalStateException();
        promise = ctx.writeAndFlush(message).channel().newPromise();
        return promise;
    }

    public String getData() {
        return data;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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
                promise.setSuccess();
            }
            buf.release();
        }
    }

}
