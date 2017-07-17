package com.spy.apollo.netty.demo.demo12_traffic_shaping.mbean;

/**
 * 注册MBean，通过jconsole查看实时流量值
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-17 10:21
 * @since 1.0
 */
public interface IoAcceptorStatMBean {

    long getWrittenBytesThroughput();

    long getReadBytesThroughput();
}
