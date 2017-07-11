package com.spy.apollo.netty.spring.util;

import com.spy.apollo.netty.spring.biz.domain.Message;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.*;
import java.util.UUID;

import static com.spy.apollo.netty.spring.util.SerializationUtil.deserialize;


/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-06 14:03
 * @since 1.0
 */
@Slf4j
public class SerializationUtilTest {


    private static final String FILE_PATH = "c:/serialization.txt";

    @Test
    public void run() throws IOException {
        Message msg = new Message();
        msg.setKey(UUID.randomUUID().toString())
           .setAction("ADD");
//        msg.setParam("a=1");

        byte[]             bytes = SerializationUtil.serialize(msg);
        ObjectOutputStream oos   = new ObjectOutputStream(new FileOutputStream(FILE_PATH));

        try {
//            oos.writeObject(msg);
            oos.write(bytes);
            oos.flush();
        } finally {
            oos.close();
        }
        System.out.println(bytes);

        Message m = deserialize(bytes, Message.class);
        System.out.println(m);
    }


    @Test
    public void run2() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_PATH));

        try {
            int    length   = in.available();
            byte[] dataRead = new byte[length];
            in.readFully(dataRead);


            Message msg = deserialize(dataRead, Message.class);

            System.out.print(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
