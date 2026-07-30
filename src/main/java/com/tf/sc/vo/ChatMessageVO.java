package com.tf.sc.vo;

import lombok.Data;

@Data
public class ChatMessageVO {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Integer type;
    private Integer isRead;
    private String createdAt;
}
