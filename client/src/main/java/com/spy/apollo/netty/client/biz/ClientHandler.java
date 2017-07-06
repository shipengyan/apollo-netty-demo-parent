package com.spy.apollo.netty.client.biz;

import io.netty.channel.ChannelInboundHandler;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-06 7:43
 * @since 1.0
 */

public interface ClientHandler extends ChannelInboundHandler {

    void sendMsg(Object obj);
}
