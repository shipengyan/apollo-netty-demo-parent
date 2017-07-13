package com.spy.apollo.netty.demo.netty;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-12 17:38
 * @since 1.0
 */
@Slf4j
public class ThreadLocalCompareTest {

    @Test
    public void jdkThreadLocalTest() {
        final int                   threadLocalCount = 1000;
        final ThreadLocal<String>[] caches           = new ThreadLocal[threadLocalCount];
        final Thread                mainThread       = Thread.currentThread();

        for (int i = 0; i < threadLocalCount; i++) {
            caches[i] = new ThreadLocal();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < threadLocalCount; i++) {
                    caches[i].set("float.lu");
                }
                long start = System.nanoTime();
                for (int i = 0; i < threadLocalCount; i++) {
                    for (int j = 0; j < 1000000; j++) {
                        caches[i].get();
                    }
                }
                long end = System.nanoTime();
                System.out.println("take[" + TimeUnit.NANOSECONDS.toMillis(end - start) + "]ms");
                LockSupport.unpark(mainThread);
            }

        });
        t.start();
        LockSupport.park(mainThread);
    }

    @Test
    public void fastThreadLocalTest() {
        final int                       threadLocalCount = 1000;
        final FastThreadLocal<String>[] caches           = new FastThreadLocal[threadLocalCount];
        final Thread                    mainThread       = Thread.currentThread();

        for (int i = 0; i < threadLocalCount; i++) {
            caches[i] = new FastThreadLocal();
        }

        Thread t = new FastThreadLocalThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < threadLocalCount; i++) {
                    caches[i].set("float.lu");
                }
                long start = System.nanoTime();
                for (int i = 0; i < threadLocalCount; i++) {
                    for (int j = 0; j < 1000000; j++) {
                        caches[i].get();
                    }
                }
                long end = System.nanoTime();
                System.out.println("take[" + TimeUnit.NANOSECONDS.toMillis(end - start) + "]ms");
                LockSupport.unpark(mainThread);
            }

        });
        t.start();
        LockSupport.park(mainThread);
    }


    @Test
    public void fastThreadLocalTest1() {
        FastThreadLocal<String> threadLocal = new FastThreadLocal<>();

        threadLocal.set("abc");
        threadLocal.set("de");
    }

}
