package com.spy.apollo.netty.demo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-17 12:59
 * @since 1.0
 */
@Slf4j
public class ByteBuffTest {

    private static final int loop = 3000000;

    private static final String content = "helloworld";

    private ByteBuf byteBuf;
    private long    begin;

    @Before
    public void before() {
        byteBuf = null;
        begin = System.currentTimeMillis();
    }

    @Test
    public void poolTest() {
        log.debug("pool test");
        for (int i = 0; i < loop; i++) {
            byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(1024);
            byteBuf.writeBytes(content.getBytes(CharsetUtil.UTF_8));
            byteBuf.release();
        }
        log.debug("cost={}ms", (System.currentTimeMillis() - begin));
    }

    @Test
    public void unpoolTest() {
        log.debug("unpool test");
        for (int i = 0; i < loop; i++) {
            //byteBuf = UnpooledByteBufAllocator.DEFAULT.directBuffer(1024);
            byteBuf = Unpooled.directBuffer(1024);
            byteBuf.writeBytes(content.getBytes(CharsetUtil.UTF_8));
            byteBuf.release();
        }
        log.debug("cost={}ms", System.currentTimeMillis() - begin);
    }

}
