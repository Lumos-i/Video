package com.video.controller.webSocket;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author zrq
 * @ClassName WsSever
 * @date 2022/11/2 17:29
 * @Description TODO
 */

@ServerEndpoint("/webSocket/video/{videoId}")
@Component
@Slf4j
public class WsServer {

    private static final Map<Integer, Set<Session>> videos = new ConcurrentHashMap();

    @OnOpen
    public void connect(@PathParam("videoId") Integer videoId, Session session) throws Exception {
        // 将session按照视频id进行存储
        if (!videos.containsKey(videoId)) {
            // 判断视频id是否存在，不存在则添加
            Set<Session> video = new HashSet<>();
            // 添加用户
            video.add(session);
            videos.put(videoId, video);
        } else {
            // 视频id，已存在直接将用户添加进去
            videos.get(videoId).add(session);
        }
        log.info("已连接...");
    }

    @OnClose
    public void disConnect(@PathParam("videoId") Integer videoId, Session session) {
        videos.get(videoId).remove(session);
        System.out.println("已退出...");
    }

    @OnMessage
    public void receiveMsg(@PathParam("videoId") Integer videoId,
                           String msg) throws Exception {
        // 推送消息
        broadcast(videoId, msg);
    }

    // 按照视频id进行推送
    public static void broadcast(Integer videoId, String msg) throws Exception {
        for (Session session : videos.get(videoId)) {
            session.getBasicRemote().sendText(msg);
        }
    }

}