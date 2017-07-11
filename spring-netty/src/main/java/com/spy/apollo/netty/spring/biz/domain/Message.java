package com.spy.apollo.netty.spring.biz.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-06 14:03
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class Message implements Serializable {

    private String key;
    private String version = "1.0.0";
    private String action;
    private String param;
    private Long sendTime = System.currentTimeMillis();
}
