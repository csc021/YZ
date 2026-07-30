package com.tf.sc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tf.sc.dto.request.ChatMessageRequest;
import com.tf.sc.dto.response.ConversationResponse;
import com.tf.sc.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService extends IService<ChatMessage> {
    ChatMessage send(ChatMessageRequest request);

    List<ChatMessage> history(Long senderId, Long receiverId);

    long unreadCount(Long userId);

    boolean markRead(Long senderId, Long receiverId);

    List<ConversationResponse> conversations(Long userId);
}
