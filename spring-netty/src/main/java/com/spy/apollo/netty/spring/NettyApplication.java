package com.spy.apollo.netty.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

/**
 * server && client bootstarp
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-05 21:14
 * @since 1.0
 */
@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.spy.apollo.netty.spring")
public class NettyApplication {

    public static void main(String[] args) {
        //new SpringApplicationBuilder(ServerApplication.class).web(false).run(args);
//        SpringApplication.run(ServerApplication.class, args);
        SpringApplication app = new SpringApplication(NettyApplication.class);
        app.setWebEnvironment(false);
        app.run(args);

        try {
            System.in.read();
        } catch (IOException e) {
            log.error("sys in read", e);
        }

    }

}
