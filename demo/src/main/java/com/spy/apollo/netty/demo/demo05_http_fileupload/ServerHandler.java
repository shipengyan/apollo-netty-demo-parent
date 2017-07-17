package com.spy.apollo.netty.demo.demo05_http_fileupload;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.Date;

import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-11 21:34
 * @since 1.0
 */
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    private ChannelHandlerContext ctx;


    // Factory that writes to disk
    private static final HttpDataFactory factory          = new DefaultHttpDataFactory(true);
    private static final String          FILE_UPLOAD_LOCN = "c:/";

    private HttpRequest            httpRequest;
    private HttpPostRequestDecoder httpDecoder;

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final HttpObject httpObject) throws Exception {
        if (httpObject instanceof HttpRequest) {
            httpRequest = (HttpRequest) httpObject;
            final URI uri = new URI(httpRequest.getUri());

            System.out.println("Got URI " + uri);
            if (httpRequest.getMethod() == POST) {
                httpDecoder = new HttpPostRequestDecoder(factory, httpRequest);
                httpDecoder.setDiscardThreshold(0);
            } else {
                sendResponse(ctx, METHOD_NOT_ALLOWED, null);
            }
        }

        boolean readingChunks = HttpHeaders.isTransferEncodingChunked(httpRequest);
        log.debug("reading chunks={}", readingChunks);

        if (httpDecoder != null) {
            if (httpObject instanceof HttpContent) {
                if (httpObject instanceof LastHttpContent) {
                    resetPostRequestDecoder();
                    return;
                }
                final HttpContent chunk = (HttpContent) httpObject;
                httpDecoder.offer(chunk);
                readChunk(ctx);

                if (chunk instanceof LastHttpContent) {
                    resetPostRequestDecoder();
                }
            }
        }
    }

    private void readChunk(ChannelHandlerContext ctx) throws IOException {
        while (httpDecoder.hasNext()) {
            InterfaceHttpData data = httpDecoder.next();
            if (data != null) {
                try {
                    switch (data.getHttpDataType()) {
                        case Attribute:
                            log.debug("attr {}", data.getName());
                            break;
                        case FileUpload:
                            log.debug("file upload");

                            final FileUpload fileUpload = (FileUpload) data;
                            String timestamp = DateFormatUtils.format(new Date(), "yyyyMMddhhmmss");

                            final File file = new File(FILE_UPLOAD_LOCN + fileUpload.getFilename() + "." + timestamp);
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            System.out.println("Created file " + file);
                            try (FileChannel inputChannel = new FileInputStream(fileUpload.getFile()).getChannel();
                                 FileChannel outputChannel = new FileOutputStream(file).getChannel()) {
                                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                                sendResponse(ctx, CREATED, "file name: " + file.getAbsolutePath());
                            }

                            // clean
                            httpDecoder.removeHttpDataFromClean(fileUpload);
                            break;
                    }
                } finally {
                    data.release();
                }
            }
        }
    }

    /**
     * Sends a response back.
     *
     * @param ctx
     * @param status
     * @param message
     */
    private static void sendResponse(ChannelHandlerContext ctx, HttpResponseStatus status, String message) {
        final FullHttpResponse response;
        String                 msgDesc = message;
        if (message == null) {
            msgDesc = "Failure: " + status;
        }
        msgDesc += " \r\n";

        final ByteBuf buffer = Unpooled.copiedBuffer(msgDesc, CharsetUtil.UTF_8);
        if (status.code() >= HttpResponseStatus.BAD_REQUEST.code()) {
            response = new DefaultFullHttpResponse(HTTP_1_1, status, buffer);
        } else {
            response = new DefaultFullHttpResponse(HTTP_1_1, status, buffer);
        }

        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

        // Close the connection as soon as the response is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void resetPostRequestDecoder() {
        httpRequest = null;
        httpDecoder.destroy();
        httpDecoder = null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (httpDecoder != null) {
            httpDecoder.cleanFiles();
        }
    }
}
