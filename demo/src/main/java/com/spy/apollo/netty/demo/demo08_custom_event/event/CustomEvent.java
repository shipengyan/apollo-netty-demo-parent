package com.spy.apollo.netty.demo.demo08_custom_event.event;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-14 11:41
 * @since 1.0
 */
@Slf4j
@Data
@Accessors(chain = true)
public class CustomEvent {

    private String data;

}
