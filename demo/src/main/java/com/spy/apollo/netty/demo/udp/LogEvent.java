package com.spy.apollo.netty.demo.udp;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 13:25
 * @since 1.0
 */
@Slf4j
@Data
@Accessors(chain = true)
public class LogEvent {

    public static final byte SEPARATOR = (byte) ':';

    private final InetSocketAddress source;
    private final String            logfile;
    private final String            msg;
    private final long              received;

    public LogEvent(String logfile, String msg) { //1
        this(null, -1, logfile, msg);
    }

    public LogEvent(InetSocketAddress source, long received, String logfile, String msg) {  //2
        this.source = source;
        this.logfile = logfile;
        this.msg = msg;
        this.received = received;
    }
}
