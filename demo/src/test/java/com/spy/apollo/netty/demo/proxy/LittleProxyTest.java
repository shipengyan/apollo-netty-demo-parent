package com.spy.apollo.netty.demo.proxy;

import com.spy.apollo.netty.demo.common.Const;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.io.IOException;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-13 14:15
 * @since 1.0
 */
@Slf4j
public class LittleProxyTest {


    @Test
    public void run() throws IOException {
        DefaultHttpProxyServer
            .bootstrap()
            .withPort(Const.PORT)
            .withFiltersSource(new HttpFiltersSourceAdapter() {
                @Override
                public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                    return new HttpFiltersAdapter(originalRequest, ctx) {
                        @Override
                        public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                            log.debug("client to proxy request");

                            if (httpObject instanceof DefaultHttpRequest) {
                                DefaultHttpRequest request = (DefaultHttpRequest) httpObject;

                                log.debug("uri={}", request.getUri());
                            }


                            return null;
                        }

                        @Override
                        public HttpObject serverToProxyResponse(HttpObject httpObject) {
                            log.debug("server to proxy response");

                            return httpObject;
                        }
                    };
                }
            })
            .start();

        log.debug("在浏览器中配置网络代理{}:{},即可代理", Const.HOST, Const.PORT);
        System.in.read();
    }
}
