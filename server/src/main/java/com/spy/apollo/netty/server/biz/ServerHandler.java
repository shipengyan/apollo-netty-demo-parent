package com.spy.apollo.netty.server.biz;

import io.netty.channel.ChannelInboundHandler;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-06 7:27
 * @since 1.0
 */
public interface ServerHandler extends ChannelInboundHandler {

    void sendMsg(Object obj);
}
