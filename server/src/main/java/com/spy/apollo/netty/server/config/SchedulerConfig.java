package com.spy.apollo.netty.server.config;

import com.alibaba.fastjson.JSONObject;
import com.spy.apollo.netty.server.biz.service.ServerHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-06 9:13
 * @since 1.0
 */
@Slf4j
@Component
public class SchedulerConfig {

    @Autowired
    private ServerHandler serverHandler;

    //@Scheduled(cron = "0/50 * * * * ?")
    public void sendMsgJob() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("random", Math.random());
        jsonObject.put("uuid", UUID.randomUUID());
        jsonObject.put("type", "server");

        serverHandler.sendMsg(jsonObject);
    }


    //@Scheduled(cron = "0/10 * * * * ?")
    public void sendMsgSyncJob() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("random", Math.random());
        jsonObject.put("uuid", UUID.randomUUID());
        jsonObject.put("type", "server");

        serverHandler.sendMsgSync(jsonObject);
    }

}
