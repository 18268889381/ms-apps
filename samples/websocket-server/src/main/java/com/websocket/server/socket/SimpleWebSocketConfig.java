package com.websocket.server.socket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket配置
 */
@EnableScheduling
@Configuration
public class SimpleWebSocketConfig implements DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleWebSocketConfig.class);

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void destroy() throws Exception {
        LOG.info("Spring Application被销毁，关闭在线的WebSocket会话");
        // 当Spring容器被销毁时,关闭此次会话
        int onlineCount = EchoServer.getOnlineCount();
        if (onlineCount > 0) {
            EchoServer.copyWebSocketServer()
                    .forEach(server -> {
                        try {
                            server.send("即将关闭此会话", false);
                            server.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    @Scheduled(fixedRate = 2000)
    public void schedule() {
        if (!EchoServer.hasOnline()) {
            return;
        }

        if (counter.getAndIncrement() > 5) {
            System.exit(0);
        }

        EchoServer.copyWebSocketServer()
                .forEach(server -> {
                    try {
                        server.send("服务器发来问候: " + new Date().toLocaleString(), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private final AtomicInteger counter = new AtomicInteger(0);

}
