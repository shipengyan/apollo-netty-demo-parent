package com.spy.apollo.netty.demo.demo03_memcached_codec;

import com.spy.apollo.netty.demo.demo03_memcached_codec.codec.MemcachedRequestEncoder;
import com.spy.apollo.netty.demo.demo03_memcached_codec.memcached.MemcachedRequest;
import com.spy.apollo.netty.demo.demo03_memcached_codec.memcached.Opcode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * 编码测试（出站）
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-12 13:57
 * @since 1.0
 */
@Slf4j
public class MemcachedRequestEncoderTest {

    /**
     * 编码测试
     */
    @Test
    public void encodeTest() {
        MemcachedRequest request = new MemcachedRequest(Opcode.SET, "key1", "value1"); //1

        EmbeddedChannel channel = new EmbeddedChannel(new MemcachedRequestEncoder());  //2
        channel.writeOutbound(request); //3

        ByteBuf encoded = (ByteBuf) channel.readOutbound();

        Assert.assertNotNull(encoded);  //4
        Assert.assertEquals(request.magic(), encoded.readUnsignedByte());  //5
        Assert.assertEquals(request.opCode(), encoded.readByte());  //6
        Assert.assertEquals(4, encoded.readShort());//7
        Assert.assertEquals((byte) 0x08, encoded.readByte()); //8
        Assert.assertEquals((byte) 0, encoded.readByte());//9
        Assert.assertEquals(0, encoded.readShort());//10
        Assert.assertEquals(4 + 6 + 8, encoded.readInt());//11
        Assert.assertEquals(request.id(), encoded.readInt());//12
        Assert.assertEquals(request.cas(), encoded.readLong());//13
        Assert.assertEquals(request.flags(), encoded.readInt()); //14
        Assert.assertEquals(request.expires(), encoded.readInt()); //15

        byte[] data = new byte[encoded.readableBytes()]; //16
        encoded.readBytes(data);
        Assert.assertArrayEquals((request.key() + request.body()).getBytes(CharsetUtil.UTF_8), data);
        Assert.assertFalse(encoded.isReadable());  //17

        Assert.assertFalse(channel.finish());
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void decodeTest() {

    }
}
