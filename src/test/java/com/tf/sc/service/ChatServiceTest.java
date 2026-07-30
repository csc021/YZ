package com.tf.sc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.tf.sc.dto.request.ChatMessageRequest;
import com.tf.sc.entity.ChatMessage;
import com.tf.sc.mapper.ChatMessageMapper;
import com.tf.sc.service.impl.ChatMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock
    private ChatMessageMapper chatMessageMapper;

    @Spy
    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(chatMessageService, "baseMapper", chatMessageMapper);
    }

    @Test
    void sendDefaultsTextMessageToUnreadAndSaves() {
        doReturn(true).when(chatMessageService).save(any(ChatMessage.class));
        ChatMessageRequest request = new ChatMessageRequest();
        request.setSenderId(1L);
        request.setReceiverId(2L);
        request.setContent("hello");

        ChatMessage result = chatMessageService.send(request);

        assertEquals(1L, result.getSenderId());
        assertEquals(2L, result.getReceiverId());
        assertEquals("hello", result.getContent());
        assertEquals(0, result.getType());
        assertEquals(0, result.getIsRead());
        assertNotNull(result.getCreatedAt());
        verify(chatMessageService).save(result);
    }

    @Test
    void historyDelegatesToMapper() {
        ChatMessage message = new ChatMessage();
        when(chatMessageMapper.selectChatHistory(1L, 2L)).thenReturn(Collections.singletonList(message));

        List<ChatMessage> result = chatMessageService.history(1L, 2L);

        assertEquals(1, result.size());
        assertSame(message, result.get(0));
        verify(chatMessageMapper).selectChatHistory(1L, 2L);
    }

    @Test
    void markReadUpdatesUnreadMessages() {
        doReturn(true).when(chatMessageService).update(any(Wrapper.class));

        boolean success = chatMessageService.markRead(1L, 2L);

        assertTrue(success);
        verify(chatMessageService).update(any(Wrapper.class));
    }
}
