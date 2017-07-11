package com.spy.apollo.netty.spring.biz.protocol;

import com.spy.apollo.netty.spring.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-06 13:34
 * @since 1.0
 */
@Slf4j
public class Encode extends MessageToByteEncoder {

    private Class<?> genericClass;

    public Encode(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (genericClass.isInstance(msg)) {
            byte[] bytes = SerializationUtil.serialize(msg);

            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }
}
