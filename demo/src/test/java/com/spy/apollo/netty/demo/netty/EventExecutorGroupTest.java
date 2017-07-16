package com.spy.apollo.netty.demo.netty;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-15 14:05
 * @since 1.0
 */
@Slf4j
public class EventExecutorGroupTest {

    private static final int           MAX_THREAD = 10000;
    private static       AtomicInteger count      = new AtomicInteger(0);

    @Test
    public void run() throws IOException {
        final Thread mainThread = Thread.currentThread();

        final long start = System.currentTimeMillis();

        EventExecutorGroup group = new DefaultEventExecutorGroup(4); // 4 threads

        for (int i = 0; i < MAX_THREAD; i++) {
            Future<?> f = group.submit(new Runnable() {
                @Override
                public void run() {
                    log.debug("thread name={},id={}", Thread.currentThread().getName(), Thread.currentThread().getId());
                }
            });
            f.addListener(new FutureListener() {

                @Override
                public void operationComplete(Future future) throws Exception {
                    log.debug("op complete {}", count.get());
                    if (count.incrementAndGet() >= MAX_THREAD) {

                        log.debug("cost={}ms", (System.currentTimeMillis() - start));
                        LockSupport.unpark(mainThread);
                    }
                }
            });
        }

        LockSupport.park(mainThread);

    }
}
