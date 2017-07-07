package com.spy.apollo.netty.client;

import com.spy.apollo.netty.client.util.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-07 7:34
 * @since 1.0
 */
@Slf4j
public class SyncHttpResponse {

    @Test
    public void run() throws Exception {
        HttpClient client = new HttpClient("https://www.baidu.com/");

        client.connect();
        String body = client.getBody();

        System.out.println(body);
    }

}
