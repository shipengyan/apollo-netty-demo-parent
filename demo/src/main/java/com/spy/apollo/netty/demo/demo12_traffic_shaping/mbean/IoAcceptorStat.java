package com.spy.apollo.netty.demo.demo12_traffic_shaping.mbean;

import com.spy.apollo.netty.demo.demo12_traffic_shaping.Server;
import lombok.extern.slf4j.Slf4j;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-17 10:22
 * @since 1.0
 */
@Slf4j
public class IoAcceptorStat implements IoAcceptorStatMBean {
    @Override
    public long getWrittenBytesThroughput() {
        return Server.getInstance()
                     .globalChannelTrafficShapingHandler()
                     .trafficCounter()
                     .lastWriteThroughput();
    }

    @Override
    public long getReadBytesThroughput() {
        return Server.getInstance()
                     .globalChannelTrafficShapingHandler()
                     .trafficCounter()
                     .lastReadThroughput();
    }
}
