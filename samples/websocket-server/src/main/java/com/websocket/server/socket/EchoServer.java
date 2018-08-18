package com.websocket.server.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket连接的服务端
 */
@Component
@ServerEndpoint("/web-socket-endpoint")
public class EchoServer {
    private static final Logger log = LoggerFactory.getLogger(EchoServer.class);

    private static final CopyOnWriteArraySet<EchoServer> WEB_SOCKET_CACHE = new CopyOnWriteArraySet<>();

    private volatile Session session;

    public EchoServer() {
        // ~
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("sessionId: {}, requestURI: {}", session.getId(), session.getRequestURI());
        this.session = session;
        // 增加连接
        incrementOnline(this);
        log.info("new connection...current online count: {}", getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("message received: {}", message);
    }

    @OnClose
    public void onClose() {
        // 连接关闭
        decrementOnline(this);
        log.info("one connection closed, session: {}", session.getId());
        this.session = null;
    }

    @OnError
    public void onError(Throwable e) {
        // 连接关闭
        log.error("exception: {}", e.getMessage(), e);
    }

    private void setSession(Session session) {
        this.session = session;
    }

    /**
     * 获取当前连接的Session
     */
    public Session getSession() {
        return session;
    }

    /**
     * 是否打开
     */
    public boolean isOpen() {
        return isOpen(session);
    }

    /**
     * 关闭会话
     */
    public void close() {
        close(null);
    }

    /**
     * 关闭会话
     */
    public void close(CloseReason.CloseCode closeCode, String reason) {
        close(new CloseReason(closeCode, reason));
    }

    private void close(CloseReason reason) {
        Session session = getSession();
        if (isOpen(session)) {
            try {
                if (reason != null) {
                    session.close(reason);
                } else {
                    session.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送消息
     *
     * @param payload 数据
     * @param async   是否为异步
     * @throws IOException
     */
    public void send(String payload, boolean async) throws IOException {
        send(payload, getSession(), async);
    }

    /**
     * 发送消息
     *
     * @param payload 数据
     * @param async   是否为异步
     * @throws IOException
     */
    public void send(byte[] payload, boolean async) throws IOException {
        send(payload, getSession(), async);
    }

    /**
     * 发送消息
     *
     * @param payload 数据
     * @param session 回话
     * @param async   是否为异步
     * @throws IOException
     */
    public static void send(String payload, Session session, boolean async) throws IOException {
        if (isOpen(session)) {
            if (async) {
                session.getAsyncRemote().sendText(payload);
            } else {
                session.getBasicRemote().sendText(payload);
            }
        }
    }

    /**
     * 发送消息
     *
     * @param buffer  数据
     * @param session 回话
     * @param async   是否为异步
     * @throws IOException
     */
    public static void send(ByteBuffer buffer, Session session, boolean async) throws IOException {
        if (isOpen(session)) {
            if (async) {
                session.getAsyncRemote().sendBinary(buffer);
            } else {
                session.getBasicRemote().sendBinary(buffer);
            }
        }
    }

    public static void send(byte[] payload, Session session, boolean async) throws IOException {
        if (payload != null && payload.length > 0 && isOpen(session)) {
            ByteBuffer buffer = ByteBuffer.allocate(payload.length);
            buffer.put(payload);
            send(buffer, session, async);
        }
    }

    /**
     * Session是否开启
     */
    public static boolean isOpen(Session session) {
        return session != null && session.isOpen();
    }

    /**
     * 新上线的Session
     */
    public int incrementOnline(EchoServer server) {
        WEB_SOCKET_CACHE.add(server);
        return WEB_SOCKET_CACHE.size();
    }

    /**
     * 客户端下线
     */
    public static int decrementOnline(EchoServer server) {
        WEB_SOCKET_CACHE.remove(server);
        return WEB_SOCKET_CACHE.size();
    }

    /**
     * 获取当前在线的WebSocket数
     */
    public static int getOnlineCount() {
        return WEB_SOCKET_CACHE.size();
    }

    /**
     * 拷贝当前的WebSocket
     */
    public static List<EchoServer> copyWebSocketServer() {
        return new ArrayList<>(WEB_SOCKET_CACHE);
    }

    /**
     * 是否有在线用户
     */
    public static boolean hasOnline() {
        return getOnlineCount() > 0;
    }
}
