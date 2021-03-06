package com.spy.apollo.netty.demo.common;

import io.netty.util.AttributeKey;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 21:36
 * @since 1.0
 */
public interface Const {

    String  HOST = "localhost";
    Integer PORT = 8899;

    AttributeKey<String> STATE = AttributeKey.valueOf("myHandler.state");


}
