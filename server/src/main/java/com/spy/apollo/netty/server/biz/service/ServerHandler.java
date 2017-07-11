package com.spy.apollo.netty.server.biz.service;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelInboundHandler;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-06 7:27
 * @since 1.0
 */
public interface ServerHandler extends ChannelInboundHandler {

    /**
     * 异步发送消息
     *
     * @param obj
     */
    void sendMsg(Object obj);

    /**
     * 同步发送消息
     *
     * @param obj
     * @return
     */
    JSONObject sendMsgSync(Object obj);


}
