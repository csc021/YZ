package com.tf.sc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tf.sc.dto.request.ChatMessageRequest;
import com.tf.sc.dto.response.ConversationResponse;
import com.tf.sc.entity.ChatMessage;
import com.tf.sc.entity.User;
import com.tf.sc.mapper.ChatMessageMapper;
import com.tf.sc.service.ChatMessageService;
import com.tf.sc.service.UserService;
import com.tf.sc.utils.BeanCopyUtil;
import com.tf.sc.utils.DateUtil;
import com.tf.sc.websocket.ChatWebSocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    private static final Logger log = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    @Autowired
    private UserService userService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public ChatMessage send(ChatMessageRequest request) {
        ChatMessage message = new ChatMessage();
        BeanCopyUtil.copy(request, message);
        if (message.getType() == null) {
            message.setType(0);
        }
        message.setIsRead(0);
        message.setCreatedAt(DateUtil.nowStr());
        save(message);

        // 持久化后通过 WebSocket 实时推送给接收方
        try {
            String json = objectMapper.writeValueAsString(message);
            ChatWebSocket.pushToUser(String.valueOf(message.getReceiverId()), json);
        } catch (Exception e) {
            // 推送失败不影响主流程，接收方可通过轮询兜底
            log.warn("WebSocket 推送消息失败: {}", e.getMessage());
        }

        return message;
    }

    @Override
    public List<ChatMessage> history(Long senderId, Long receiverId) {
        return baseMapper.selectChatHistory(senderId, receiverId);
    }

    @Override
    public long unreadCount(Long userId) {
        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("receiver_id", userId).eq("is_read", 0);
        return count(wrapper);
    }

    @Override
    @Transactional
    public boolean markRead(Long senderId, Long receiverId) {
        UpdateWrapper<ChatMessage> wrapper = new UpdateWrapper<>();
        wrapper.eq("sender_id", senderId)
                .eq("receiver_id", receiverId)
                .eq("is_read", 0)
                .set("is_read", 1);
        return update(wrapper);
    }

    @Override
    public List<ConversationResponse> conversations(Long userId) {
        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("sender_id", userId).or().eq("receiver_id", userId).orderByDesc("created_at");
        List<ChatMessage> messages = list(wrapper);
        Map<Long, ConversationResponse> conversations = new LinkedHashMap<>();
        for (ChatMessage message : messages) {
            Long peerId = userId.equals(message.getSenderId()) ? message.getReceiverId() : message.getSenderId();
            ConversationResponse response = conversations.get(peerId);
            if (response == null) {
                response = new ConversationResponse();
                response.setPeerUserId(peerId);
                response.setLastMessageId(message.getId());
                response.setLastContent(message.getContent());
                response.setLastType(message.getType());
                response.setLastIsRead(message.getIsRead());
                response.setLastCreatedAt(message.getCreatedAt());
                User peer = userService.findById(peerId);
                if (peer != null) {
                    response.setPeerUsername(peer.getUsername());
                    response.setPeerNickname(peer.getNickname());
                    response.setPeerAvatar(peer.getAvatar());
                    response.setPeerEmployeeNo(peer.getEmployeeNo());
                }
                conversations.put(peerId, response);
            }
            if (userId.equals(message.getReceiverId()) && Integer.valueOf(0).equals(message.getIsRead())) {
                response.setUnreadCount(response.getUnreadCount() + 1);
            }
        }
        return new ArrayList<>(conversations.values());
    }
}
