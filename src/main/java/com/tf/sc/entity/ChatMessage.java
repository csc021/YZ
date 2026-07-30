package com.tf.sc.entity;

import lombok.Data;

@Data
public class ChatMessage {
    private Long id;
    /** 发送者ID */
    private Long senderId;
    /** 接收者ID */
    private Long receiverId;
    /** 消息内容 */
    private String content;
    /** 消息类型：0文字 1图片 */
    private Integer type;
    /** 是否已读：0未读 1已读 */
    private Integer isRead;
    /** 创建时间 */
    private String createdAt;
}