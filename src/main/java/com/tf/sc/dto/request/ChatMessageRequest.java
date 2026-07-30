package com.tf.sc.dto.request;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private Long senderId;
    private Long receiverId;
    private String content;
    private Integer type;
}
