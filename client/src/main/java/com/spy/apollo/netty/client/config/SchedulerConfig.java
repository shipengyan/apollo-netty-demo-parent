package com.spy.apollo.netty.client.config;

import com.spy.apollo.netty.client.biz.ClientHandler;
import com.spy.apollo.netty.core.domain.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 9:39
 * @since 1.0
 */
@Slf4j
@Component
public class SchedulerConfig {

    @Autowired
    private ClientHandler clientHandler;

    //@Scheduled(cron = "0/50 * * * * ?")
    public void sendMsg() {
        Message msg = new Message();
        msg.setKey(UUID.randomUUID().toString());
        msg.setAction("add");

        clientHandler.sendMsg(msg);
    }
}
