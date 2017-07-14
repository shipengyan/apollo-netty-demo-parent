package com.spy.apollo.netty.demo.demo09_file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

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

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;

        String readStr = byteBuf.toString(CharsetUtil.UTF_8);
        log.debug("Server received: {}", readStr);

        //TODO
        log.debug("channel is writable={}", ctx.channel().isWritable());

        ReferenceCountUtil.release(msg);

        // check whether is get one file
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(readStr);
        } catch (Exception e) {
            log.error("invalid json", e);
            return;
        }

        String action = jsonObject.getString("action");
        if ("file".equalsIgnoreCase(action)) {
            File            file = new File("c:/biz.txt"); //TODO you should change it
            FileInputStream in   = new FileInputStream(file);

            byte[] buffer = new byte[1024 * 100];

            BufferedInputStream bis = new BufferedInputStream(in);
            int                 n   = 0;
            while ((n = in.read(buffer)) != -1) {
                byteBuf = Unpooled.buffer(buffer.length);
                //这里读取到多少，就发送多少，是为了防止最后一次读取没法满填充buffer，
                //导致将buffer中的处于尾部的上一次遗留数据也发送走
                byteBuf.writeBytes(buffer, 0, n);
                ctx.write(byteBuf);
                byteBuf.clear();
            }
            ctx.flush();
            log.info("send file over");
        }


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
