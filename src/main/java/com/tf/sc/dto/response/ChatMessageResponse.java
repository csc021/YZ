package com.tf.sc.dto.response;

import lombok.Data;

@Data
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Integer type;
    private Integer isRead;
    private String createdAt;
}
