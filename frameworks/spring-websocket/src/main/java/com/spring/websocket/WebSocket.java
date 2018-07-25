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

@ServerEndpoint("/web-socket-endpoint")
@Component
public class WebSocket {
    private static final Logger logger = LoggerFactory.getLogger(WebSocket.class);

    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);
    private static final CopyOnWriteArraySet<WebSocket> WEB_SOCKET_SET = new CopyOnWriteArraySet<>();

    private Session currentSession;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.currentSession = session;
        WEB_SOCKET_SET.add(this);
        incrementOnlineCount();
        for (WebSocket item : WEB_SOCKET_SET) {
            if (!item.equals(this)) {
                //send to others only.
                item.sendMessage("someone just joined in.");
            }
        }
        logger.info("new connection...current online count: {}", getOnlineCount());
    }

    @OnClose
    public void onClose() throws IOException {
        WEB_SOCKET_SET.remove(this);
        decOnlineCount();
        for (WebSocket item : WEB_SOCKET_SET) {
            item.sendMessage("someone just closed a connection.");
        }
        logger.info("one connection closed...current online count: {}", getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        logger.info("message received: {}", message);
        // broadcast received message
        for (WebSocket socket : WEB_SOCKET_SET) {
            socket.sendMessage(message);
        }
    }

    public void sendMessage(String message) throws IOException {
        this.currentSession.getBasicRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return ONLINE_COUNT.get();
    }

    public static synchronized int incrementOnlineCount() {
        return ONLINE_COUNT.incrementAndGet();
    }

    public static synchronized int decOnlineCount() {
        return ONLINE_COUNT.decrementAndGet();
    }

}
