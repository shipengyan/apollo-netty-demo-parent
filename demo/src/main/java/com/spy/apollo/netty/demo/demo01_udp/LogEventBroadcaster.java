package com.spy.apollo.netty.demo.demo01_udp;

import com.spy.apollo.netty.demo.demo01_udp.protocol.LogEventEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * udp广播
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 13:32
 * @since 1.0
 */
@Slf4j
public class LogEventBroadcaster {

    private final Bootstrap      bootstrap;
    private final File           file;
    private final EventLoopGroup group;


    public LogEventBroadcaster(InetSocketAddress address, File file) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                 .channel(NioDatagramChannel.class) //UDP
                 .option(ChannelOption.SO_BROADCAST, true)
                 .handler(new LogEventEncoder(address));
        this.file = file;
    }

    public static void main(String[] args) {
        // Preconditions.checkArgument(args.length < 2, "参数不能小于2");

        LogEventBroadcaster broadcaster = new LogEventBroadcaster(new InetSocketAddress("255.255.255.255", Const.PORT), new File("c:/test.txt"));


        try {
            broadcaster.run();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            broadcaster.stop();
        }

    }

    public void run() throws IOException {
        Channel ch = bootstrap.bind(0).syncUninterruptibly().channel();
        System.out.println("LogEventBroadcaster running");

        long pointer = 0;

        while (true) {
            long fileLength = file.length();

            if (pointer > fileLength) {
                pointer = fileLength;// file was reset, 指针大于文件大小
            } else if (pointer < fileLength) {
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.seek(pointer); // 随机读写

                String line;
                while ((line = raf.readLine()) != null) {
                    ch.writeAndFlush(new LogEvent(null, -1, file.getAbsolutePath(), line));
                }

                pointer = raf.getFilePointer();
                raf.close();
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }

}
