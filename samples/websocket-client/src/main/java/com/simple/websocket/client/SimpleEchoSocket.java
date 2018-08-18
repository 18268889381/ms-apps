package com.simple.websocket.client;


import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

/**
 * @date 2018/5/18
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class SimpleEchoSocket {

//    private static final Logger

    /**
     * WebSocket 连接对象
     */
    private Session session;

    @OnWebSocketConnect
    public void onOpen(Session session) {
        System.out.printf("连接到服务端: %s\n", session);
        this.session = session;
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        System.out.printf("服务端返回消息: %s\n", message);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.printf("Connection closed: %d - %s\n", statusCode, reason);
        this.session = null;
    }

    @OnWebSocketError
    public void onError(Throwable e) {
        System.out.printf("throwable : %s \n", e);
    }


}