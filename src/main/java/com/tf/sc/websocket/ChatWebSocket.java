package com.tf.sc.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tf.sc.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Component
public class ChatWebSocket extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocket.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            reject(session);
            return;
        }
        String userId = UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .getFirst("userId");
        String token = UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .getFirst("token");
        if (userId == null || userId.trim().isEmpty() || token == null || token.trim().isEmpty()) {
            reject(session);
            return;
        }
        // 验证 token 归属
        String tokenUserId = JwtUtil.getUserId(token);
        if (tokenUserId == null || !tokenUserId.equals(userId)) {
            reject(session);
            return;
        }
        WebSocketSessionManager.add(userId, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> payload = objectMapper.readValue(message.getPayload(), new TypeReference<Map<String, Object>>() {
        });
        Object receiverId = payload.get("receiverId");
        if (receiverId == null) {
            session.sendMessage(new TextMessage("{\"type\":\"error\",\"message\":\"receiverId is required\"}"));
            return;
        }
        WebSocketSession receiver = WebSocketSessionManager.get(String.valueOf(receiverId));
        if (receiver != null && receiver.isOpen()) {
            receiver.sendMessage(message);
        }
        if (session.isOpen()) {
            session.sendMessage(new TextMessage("{\"type\":\"ack\"}"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        WebSocketSessionManager.remove(session);
    }

    /**
     * 向指定用户推送消息（供 Service 层调用，实现实时推送）
     * @param userId 目标用户ID
     * @param jsonMessage 消息JSON字符串
     */
    public static void pushToUser(String userId, String jsonMessage) {
        if (userId == null || jsonMessage == null) return;
        WebSocketSession session = WebSocketSessionManager.get(userId);
        if (session != null && session.isOpen()) {
            try {
                synchronized (session) {
                    session.sendMessage(new TextMessage(jsonMessage));
                }
            } catch (Exception e) {
                log.warn("推送消息到用户 {} 失败: {}", userId, e.getMessage());
            }
        }
    }

    private void reject(WebSocketSession session) {
        try {
            session.close(CloseStatus.POLICY_VIOLATION);
        } catch (Exception ignored) {
        }
    }
}
