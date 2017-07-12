package com.spy.apollo.netty.demo.demo03_memcached_codec.memcached;

import lombok.extern.slf4j.Slf4j;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-12 13:46
 * @since 1.0
 */
@Slf4j
public class Opcode {
    public static final byte GET    = 0x00;
    public static final byte SET    = 0x01;
    public static final byte DELETE = 0x04;
}
