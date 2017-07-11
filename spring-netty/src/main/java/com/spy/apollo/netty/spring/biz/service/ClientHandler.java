package com.spy.apollo.netty.spring.biz.service;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelInboundHandler;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-06 7:43
 * @since 1.0
 */

public interface ClientHandler extends ChannelInboundHandler {

    /**
     * 异步发送
     *
     * @param obj
     */
    void sendMsg(Object obj);

    /**
     * 同步发送
     *
     * @param obj
     * @return
     */
    JSONObject sendMsgSync(Object obj);
}
