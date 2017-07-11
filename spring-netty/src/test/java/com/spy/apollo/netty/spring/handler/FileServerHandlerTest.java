package com.spy.apollo.netty.spring.handler;

import com.spy.apollo.netty.spring.biz.handler.FileServerHandler;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Queue;

/**
 * Handler测试
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 16:57
 * @since 1.0
 */
@Slf4j
public class FileServerHandlerTest {

    @Test
    public void run() {
        EmbeddedChannel channel = new EmbeddedChannel(new FileServerHandler());

        // 向管道中写入数据
        channel.writeInbound("c:/test.txt");

        Queue<Object> queue = channel.outboundMessages();

        while (!queue.isEmpty()) {
            Object obj = queue.poll();
            if (obj != null) {
                System.out.println(obj);

                if (obj instanceof DefaultFileRegion) {
                    DefaultFileRegion fileRegion = (DefaultFileRegion) obj;


                    try {
                        final OutputStream        output        = new FileOutputStream("c:/test2.txt");
                        final WritableByteChannel outputChannel = Channels.newChannel(output);
                        fileRegion.transferTo(outputChannel, fileRegion.position());

                        log.info("文件输出成功");

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}
