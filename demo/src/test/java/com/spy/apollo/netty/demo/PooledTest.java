package com.spy.apollo.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 22:56
 * @since 1.0
 */
@Slf4j
public class PooledTest {

    private static final BlockingQueue<ByteBuf> queue = new LinkedBlockingQueue<ByteBuf>(1000);

    public static void main(String[] args) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
                        for (int k = 0; k < 100; k++) {
                            buf.writeByte(1);
                        }
                        queue.put(buf);
                        log.debug("write buf");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        log.debug("release buf");
                        ReferenceCountUtil.release(queue.take());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        System.in.read();
    }
}
