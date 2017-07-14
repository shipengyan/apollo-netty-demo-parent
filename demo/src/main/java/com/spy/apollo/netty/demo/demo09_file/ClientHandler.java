package com.spy.apollo.netty.demo.demo09_file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.spy.apollo.netty.demo.demo08_custom_event.event.CustomEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

        ctx.channel().eventLoop().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObj = new JSONObject();

                jsonObj.put("action", "file");// request file

                sendMsg(jsonObj);
            }
        }, 2, 5, TimeUnit.SECONDS);
        sendMsg("hello world!");
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("channel read");

        String filename = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        String filePath = String.format("c:/biz%s.txt", filename);

        FileOutputStream fos = new FileOutputStream(filePath, true);
//      BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(fos);
        ByteBuf buf   = (ByteBuf) msg;
        byte[]  bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        log.debug("receive content lenght={}", bytes.length);

        try {
            fos.write(bytes);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof CustomEvent) {

            CustomEvent customEvent = (CustomEvent) evt;

            log.debug("custom data={}", customEvent.getData());
        }


    }
}
