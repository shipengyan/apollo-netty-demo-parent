package com.spy.apollo.netty.demo.demo02_biz_logic;

import com.alibaba.fastjson.JSONObject;
import com.spy.apollo.netty.demo.common.Const;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 21:58
 * @since 1.0
 */
@Slf4j
public class Client {
    @Setter
    private String host;
    @Setter
    private int    port;

    private EventLoopGroup group;
    private Bootstrap      bootstrap;

    @Setter
    private ClientBizHandler clientBizHandler;

    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();

        client.setHost(Const.HOST);
        client.setPort(Const.PORT);
        ClientBizHandler handler = new ClientBizHandler();

        client.setClientBizHandler(handler);

        new Thread() {
            @Override
            public void run() {

                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int     count = 0;
                Boolean stop  = false;

                while (!stop) {
                    log.debug("send msg");
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("a", UUID.randomUUID());

                    handler.sendMsg(jsonObj);

                    jsonObj = null;

                    count++;
                    if (count == 4) {
                        stop = true;
                    }
                }
                log.debug("send over.");
            }
        }.start();

        client.start();


    }

    public void start() throws InterruptedException {
        group = new NioEventLoopGroup(1);

        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                     .channel(NioSocketChannel.class)
                     .remoteAddress(new InetSocketAddress(host, port))
                     .option(ChannelOption.SO_KEEPALIVE, true)
                     .option(ChannelOption.TCP_NODELAY, true)
                     .handler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         public void initChannel(SocketChannel ch) throws Exception {
                             //ch.pipeline().addLast("timeout", new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime, TimeUnit.SECONDS));

                             //client端 发的是request，因此要编码
                             //client端 收的是Reponse，因此要解码
                             //   ch.pipeline().addLast(new Encode(Message.class));
                             ch.pipeline()

                               .addLast("encoder", new LengthFieldPrepender(4, false))
                               .addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))

                               .addLast(new DefaultEventExecutorGroup(10), "clientBizHandler", clientBizHandler);
                         }
                     });

            ChannelFuture f = bootstrap.connect().sync();        //6

            f.channel().closeFuture().sync();            //7
        } finally {
            group.shutdownGracefully().sync();            //8
        }

    }

}
