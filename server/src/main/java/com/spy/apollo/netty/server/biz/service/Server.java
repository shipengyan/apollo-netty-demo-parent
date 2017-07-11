package com.spy.apollo.netty.server.biz.service;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-06 7:21
 * @since 1.0
 */
public interface Server {

    void start() throws InterruptedException;

    void stop();

    void restart() throws InterruptedException;
}
