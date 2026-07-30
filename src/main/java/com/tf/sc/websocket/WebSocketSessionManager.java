package com.tf.sc.websocket;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class WebSocketSessionManager {
    private static final Map<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    private WebSocketSessionManager() {
    }

    public static void add(String key, WebSocketSession session) {
        SESSIONS.put(key, session);
    }

    public static WebSocketSession get(String key) {
        return SESSIONS.get(key);
    }

    public static void remove(String key) {
        SESSIONS.remove(key);
    }

    public static void remove(WebSocketSession session) {
        SESSIONS.entrySet().removeIf(entry -> entry.getValue().getId().equals(session.getId()));
    }
}
//11//1/1/1//1/1/1/1/1/1/1/1/1/
