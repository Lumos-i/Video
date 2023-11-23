package com.video.controller.webSocket;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/websocket/{id}")
@Component
public class MyWebSocket {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<MyWebSocket>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private Integer id;
    /**
     * 连接建立成功调用的方法*/
    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("id") Integer id) {
        this.id = id;
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入:" + id + "！当前在线人数为" + getOnlineCount());
        try {
            sendMessage("连接成功，当前时间：" + new java.sql.Timestamp(System.currentTimeMillis()));
        } catch (IOException e) {
            System.out.println("IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }


    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("id") String id) {
        System.out.println("来自客户端" + id + "的消息:" + message);

        //群发消息
        for (MyWebSocket item : webSocketSet) {
            try {
                item.sendMessage(message);
                System.out.println("推送消息给:" + item.id + ",消息是===》" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }


    /**
     * 服务端给客户端发送消息
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }

    /**
     * 自定义的群发消息，我们可以在其他的类中调用该方法，然后向前台推送消息
     */
    public static void sendInfo(@PathParam("id") Integer id, String message) throws IOException {
        for (MyWebSocket item : webSocketSet) {
            try {
                //这里可以设定只推送给这个sname的，为null则全部推送
                if (id == null) {
                    item.sendMessage(message);
                    System.out.println("推送消息给:" + item.id + ",消息是===》" + message);
                } else if (Objects.equals(item.id, id)) {
                    item.sendMessage(message);
                    System.out.println("推送消息给:" + item.id + ",消息是===》" + message);
                }
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static List<Integer> onlineUser(){
        List<Integer> ids = new ArrayList<>();
        for (MyWebSocket item : webSocketSet) {
            ids.add(item.id);
        }
        return ids;
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }
}
