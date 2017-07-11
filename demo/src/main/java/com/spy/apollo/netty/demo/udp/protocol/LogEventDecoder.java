package com.spy.apollo.netty.demo.udp.protocol;

import com.spy.apollo.netty.demo.udp.LogEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 14:23
 * @since 1.0
 */
@Slf4j
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List out) throws Exception {
        log.info("decode");

        ByteBuf data = datagramPacket.content();

        int i = data.indexOf(0, data.readableBytes(), LogEvent.SEPARATOR);

        String filename = data.slice(0, i).toString(CharsetUtil.UTF_8);  //3
        String logMsg   = data.slice(i + 1, data.readableBytes()).toString(CharsetUtil.UTF_8);  //4

        LogEvent event = new LogEvent(datagramPacket.recipient(), System.currentTimeMillis(), filename, logMsg); //5

        out.add(event);
    }
}
