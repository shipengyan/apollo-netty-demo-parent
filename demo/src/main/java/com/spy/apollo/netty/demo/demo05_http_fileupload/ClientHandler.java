package com.spy.apollo.netty.demo.demo05_http_fileupload;

import com.spy.apollo.netty.demo.common.Const;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 21:58
 * @since 1.0
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    private ChannelHandlerContext ctx;

    private static final String FILE_PATH = "c:/test.txt";

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //注册上之后，10s发送消息
        EventLoop eventLoop = ctx.channel().eventLoop();

//        MsgUtil.send(ctx, "hh");

        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {

                DiskFileUpload.deleteOnExitTemporaryFile = false; // should delete file on exit (in normal exit)
                DiskFileUpload.baseDirectory = null; // system temp directory
                DiskAttribute.deleteOnExitTemporaryFile = false; // should delete file on exit (in normal exit)
                DiskAttribute.baseDirectory = null; // system temp directory

                String uri = Const.HOST + ":" + Const.PORT;

                // setup the factory: here using a mixed memory/disk based on size threshold
                HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if MINSIZE exceed

                HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri);
                HttpHeaders headers = request.headers();
                headers.set(HttpHeaders.Names.HOST, uri);
                headers.set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                headers.set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP + ',' + HttpHeaders.Values.DEFLATE);

                headers.set(HttpHeaders.Names.ACCEPT_CHARSET, "UTF-8,utf-8;q=0.7,*;q=0.7");
                headers.set(HttpHeaders.Names.ACCEPT_LANGUAGE, "fr");
                headers.set(HttpHeaders.Names.USER_AGENT, "Netty Simple Http Client side");
                headers.set(HttpHeaders.Names.ACCEPT, "text/plain,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

                try {
                    // Use the PostBody encoder
                    HttpPostRequestEncoder bodyRequestEncoder = new HttpPostRequestEncoder(factory, request, true);  // false => not multipart

                    // it is legal to add directly header or cookie into the request until finalize
//                    for (Entry<String, String> entry : headers) {
//                        request.headers().set(entry.getKey(), entry.getValue());
//                    }

                    // add Form attribute
                    bodyRequestEncoder.addBodyAttribute("getform", "POST");
                    bodyRequestEncoder.addBodyAttribute("info", "first value");
                    bodyRequestEncoder.addBodyAttribute("secondinfo", "secondvalue ���&");

                    File file = new File(FILE_PATH);
                    bodyRequestEncoder.addBodyFileUpload("myfile", file, null, true);

                    // finalize request
                    HttpRequest request1 = bodyRequestEncoder.finalizeRequest();

                    // send request
                    Channel channel = ctx.channel();
                    channel.write(request1);

                    // test if request was chunked and if so, finish the write
                    if (bodyRequestEncoder.isChunked()) {
                        channel.write(bodyRequestEncoder);
                    }
                    channel.flush();

//                    ctx.channel().writeAndFlush(request);
//                    MsgUtil.sendHttpAndClose(ctx, request);

                    bodyRequestEncoder.cleanFiles();

                } catch (Exception e) {
                    log.error("exception", e);
                }


            }
        }, 2, TimeUnit.SECONDS);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        log.debug("http obj={}", msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
    }

}
