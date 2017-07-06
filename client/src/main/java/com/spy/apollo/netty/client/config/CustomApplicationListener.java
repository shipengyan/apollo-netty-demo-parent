package com.spy.apollo.netty.client.config;

import com.spy.apollo.netty.client.biz.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 2017-02-21 11:24
 */
@Slf4j
@Component
public class CustomApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private Client client;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("=======================================");
        log.info("=         Application Ready           =");
        log.info("=======================================");

        init();
    }

    private void init() {
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
