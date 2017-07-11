package com.spy.apollo.netty.spring.biz.protocol;

import com.spy.apollo.netty.spring.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-06 14:57
 * @since 1.0
 */
@Slf4j
public class Decode extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public Decode(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        /*if (dataLength <= 0) {
            ctx.close();
        }*/
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object obj = SerializationUtil.deserialize(data, genericClass);

        out.add(obj);

    }
}
