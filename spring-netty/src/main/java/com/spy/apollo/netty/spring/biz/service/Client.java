package com.spy.apollo.netty.spring.biz.service;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-06 7:40
 * @since 1.0
 */
public interface Client {

    void start();

    void stop();

    void reconnect();
}
