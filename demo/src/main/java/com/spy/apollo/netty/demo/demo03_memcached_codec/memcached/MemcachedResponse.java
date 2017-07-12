package com.spy.apollo.netty.demo.demo03_memcached_codec.memcached;

import lombok.extern.slf4j.Slf4j;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-12 13:49
 * @since 1.0
 */
@Slf4j
public class MemcachedResponse {
    private final byte   magic;
    private final byte   opCode;
    private final short  status;
    private final int    id;
    private final long   cas;
    private final int    flags;
    private final int    expires;
    private final String key;
    private final String data;
    private       byte   dataType;

    public MemcachedResponse(byte magic, byte opCode,
                             byte dataType, short status,
                             int id, long cas,
                             int flags, int expires, String key, String data) {
        this.magic = magic;
        this.opCode = opCode;
        this.dataType = dataType;
        this.status = status;
        this.id = id;
        this.cas = cas;
        this.flags = flags;
        this.expires = expires;
        this.key = key;
        this.data = data;
    }

    public byte magic() { //2
        return magic;
    }

    public byte opCode() { //3
        return opCode;
    }

    public byte dataType() { //4
        return dataType;
    }

    public short status() {  //5
        return status;
    }

    public int id() {  //6
        return id;
    }

    public long cas() {  //7
        return cas;
    }

    public int flags() {  //8
        return flags;
    }

    public int expires() { //9
        return expires;
    }

    public String key() {  //10
        return key;
    }

    public String data() {  //11
        return data;
    }
}
