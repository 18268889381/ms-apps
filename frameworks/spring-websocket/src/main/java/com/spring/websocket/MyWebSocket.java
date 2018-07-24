package com.spring.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/my-websocket")
@Component
public class MyWebSocket {
    private static final Logger logger = LoggerFactory.getLogger(MyWebSocket.class);

    private static final AtomicInteger onlineCount = new AtomicInteger(0);
    private static final CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<>();

    private Session session;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        webSocketSet.add(this);
        incrementOnlineCount();
        for (MyWebSocket item : webSocketSet) {
            if (!item.equals(this)) { //send to others only.
                item.sendMessage("someone just joined in.");
            }
        }
        logger.info("new connection...current online count: {}", getOnlineCount());
    }

    @OnClose
    public void onClose() throws IOException {
        webSocketSet.remove(this);
        decOnlineCount();
        for (MyWebSocket item : webSocketSet) {
            item.sendMessage("someone just closed a connection.");
        }
        logger.info("one connection closed...current online count: {}", getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        logger.info("message received: {}", message);
        // broadcast received message
        for (MyWebSocket item : webSocketSet) {
            item.sendMessage(message);
        }
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount.get();
    }

    public static synchronized int incrementOnlineCount() {
        return onlineCount.incrementAndGet();
    }

    public static synchronized int decOnlineCount() {
        return onlineCount.decrementAndGet();
    }

}
