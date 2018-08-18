package com.simple.websocket.client;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.*;

/**
 * WebSocket 客户端
 */
public class Application {
    public static void main(String[] args) {

        //用来处理wss 请求3
        SslContextFactory sslContextFactory = new SslContextFactory();
        WebSocketClient client = new WebSocketClient(sslContextFactory);
        try {
            client.start();
            URI echoUri = new URI("ws://127.0.0.1:8090/web-socket-endpoint");
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            //添加请求头参数
//            request.setHeader("" , "");
            //通过注解定义监听WebSocket的事件
            SimpleEchoSocket simpleEchoSocket = new SimpleEchoSocket();
            //尝试连接
            Future<Session> sessionFuture = client.connect(simpleEchoSocket, echoUri, request);
            Session session = sessionFuture.get();

            session.getRemote().sendString("Hello World!");
//
//            executor.submit(() -> {
//                try {
//                    TimeUnit.SECONDS.sleep(random.nextInt(4) + 1);
//                    Future<Void> future = session.getRemote().sendStringByFuture("Hello WebSocket Server ....");
//                    future.get(2, TimeUnit.SECONDS); // wait for send to complete.
//                } catch (InterruptedException | ExecutionException | TimeoutException e) {
//                    e.printStackTrace();
//                }
//            });

            for (int i = 0; i < 100; i++) {
                try {

                    session.getRemote().sendString("ping");

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            //关闭连接
            if (!client.isStopped()) {
                try {
                    client.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


//    private static final ExecutorService executor = new ThreadPoolExecutor(
//            0, 2, 1, TimeUnit.MINUTES, new SynchronousQueue<>());


}